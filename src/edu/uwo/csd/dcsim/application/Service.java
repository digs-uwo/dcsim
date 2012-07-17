package edu.uwo.csd.dcsim.application;

import java.util.*;

import edu.uwo.csd.dcsim.application.workload.*;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.vm.*;

/**
 * Represents an entire Service running in the DataCentre, consisting of an incoming Workload source
 * and a set of application tiers. Each tier completes work and passes it on to the next tier, until
 * the final tier returns it to the Workload. A tier consists of one or more running Applications.
 * 
 * @author Michael Tighe
 *
 */
public class Service {

	private Workload workload; //the workload feeding this service
	private ArrayList<ServiceTier> tiers = new ArrayList<ServiceTier>(); //the tiers of the service
	
	/**
	 * Generate a set of VMAllocationRequests for the initial set of VMs run the Service
	 * 
	 * @return
	 */
	public ArrayList<VMAllocationRequest> createInitialVmRequests() {
		ArrayList<VMAllocationRequest> vmList = new ArrayList<VMAllocationRequest>();
		
		//create a VMAllocationRequest for the minimum number of replicas in each tier
		for (ServiceTier tier : tiers) {
			for (int i = 0; i < tier.minSize; ++i)
				vmList.add(new VMAllocationRequest(tier.vmDescription));
		}
		return vmList;
	}
	
	/**
	 * Get the percentage of current incoming work to the Service for which SLA is violated
	 * @return
	 */
	public double getSLAViolation() {
		double sla = 0;
		for (ServiceTier tier : tiers) {
			for (Application app : tier.getApplications()) {
				sla += app.getSLAViolatedWork();
			}
		}
		
		sla = sla / workload.getCurrentWork();
		
		return sla;
	}
	
	/**
	 * Get the percentage of all incoming work to the Service for which SLA has been violated
	 * @return
	 */
	public double getTotalSLAViolation() {
		double sla = 0;
		for (ServiceTier tier : tiers) {
			for (Application app : tier.getApplications()) {
				sla += app.getTotalSLAViolatedWork();
			}
		}
		
		sla = sla / workload.getTotalWork();
		
		return sla;
	}
	
	public boolean canShutdown() {
		
		//verify that none of the VMs in the service are currently migrating
		for (ServiceTier tier : tiers) {
			for (Application application : tier.getApplications()) {
				if (application.getVM().isMigrating()) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public void shutdownService() {
		
		for (ServiceTier tier : tiers) {
			for (Application application : new ArrayList<Application>(tier.getApplications())) {
				VM vm = application.getVM();
				VMAllocation vmAlloc = vm.getVMAllocation();
				Host host = vmAlloc.getHost();
				
				if (vm.isMigrating())
					throw new RuntimeException("Tried to shutdown migrating VM #" + vm.getId() + ". Operation not allowed in simulation.");
				
				host.deallocate(vmAlloc);
				vm.stopApplication();
				
			}
		}
	}
	
	/**
	 * Get the Workload for this Service
	 * @return
	 */
	public Workload getWorkload() {
		return workload;
	}
	
	/**
	 * Set the Workload for this Service
	 * @param workload
	 */
	public void setWorkload(Workload workload) {
		this.workload = workload;
	}
	
	/**
	 * Get the tiers that this Service consists of 
	 * @return
	 */
	public ArrayList<ServiceTier> getServiceTiers() {
		return tiers;
	}
	
	/**
	 * Add a tier to the Service
	 * @param serviceTier
	 */
	public void addServiceTier(ServiceTier serviceTier) {
		tiers.add(serviceTier);
	}
	
	/**
	 * Represents a single tier in a Service
	 * @author Michael Tighe
	 *
	 */
	public static class ServiceTier {
		
		private ApplicationTier applicationTier; //the application tier handling application instantiation and load balancing
		private VMDescription vmDescription; //the VMDescription describing the type of VM to be created to host Applications in this tier
		private int minSize; //the minimum number of replicas in this tier
		private int maxSize; //the maximum number of replicas in this tier
		
		public ServiceTier(ApplicationTier applicationTier, VMDescription vmDescription) {
			this(applicationTier, vmDescription, 1, Integer.MAX_VALUE);
		}
		
		public ServiceTier(ApplicationTier applicationTier, VMDescription vmDescription, int minSize) {
			this(applicationTier, vmDescription, minSize, Integer.MAX_VALUE);
		}
		
		public ServiceTier(ApplicationTier applicationTier, VMDescription vmDescription, int minSize, int maxSize) {
			this.applicationTier = applicationTier;
			this.vmDescription = vmDescription;
			this.minSize = minSize;
			this.maxSize = maxSize;
		}
		
		public ArrayList<Application> getApplications() {
			return applicationTier.getApplications();
		}
		
		public VMDescription getVMDescription() {
			return vmDescription;
		}
		
		public ApplicationTier getApplicationTier() {
			return applicationTier;
		}
		
		public int getMinSize() {
			return minSize;
		}
		
		public int getMaxSize() {
			return maxSize;
		}
		
		public void setMinSize(int minSize) {
			this.minSize = minSize;
		}
		
		public void setMaxSize(int maxSize) {
			this.maxSize = maxSize;
		}
		
	}
}
