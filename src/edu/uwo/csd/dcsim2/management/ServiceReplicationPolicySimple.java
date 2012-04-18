package edu.uwo.csd.dcsim2.management;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.application.*;
import edu.uwo.csd.dcsim2.application.Service.ServiceTier;
import edu.uwo.csd.dcsim2.management.action.*;
import edu.uwo.csd.dcsim2.vm.*;

public class ServiceReplicationPolicySimple extends ManagementPolicy {

	private static Logger logger = Logger.getLogger(ServiceReplicationPolicySimple.class);
	
	ArrayList<Service> services;
	long interval;
	double scaleOutThreshold;
	double scaleInThreshold;
	VMPlacementPolicy vmPlacementPolicy;
	
	public ServiceReplicationPolicySimple(ArrayList<Service> services, long interval, double scaleOutThreshold, double scaleInThreshold, VMPlacementPolicy vmPlacementPolicy) {
		this.services = services;
		this.interval = interval;
		this.scaleOutThreshold = scaleOutThreshold;
		this.scaleInThreshold = scaleInThreshold;
		this.vmPlacementPolicy = vmPlacementPolicy;
	}
	
	@Override
	public void execute() {
		ArrayList<ReplicateAction> replicateActions = new ArrayList<ReplicateAction>();
		ArrayList<ShutdownVmAction> shutdownActions = new ArrayList<ShutdownVmAction>();
		
		//for each service running in the cloud
		for (Service service : services) {
			//for each tier
			for (ServiceTier tier : service.getServiceTiers()) {
				double avgUtil = 0;
				for (Application application : tier.getApplications()) {
					avgUtil += application.getResourceInUse().getCpu() / tier.getVMDescription().getCpu();
				}
				avgUtil = avgUtil / tier.getApplications().size();
				
				//if the average utilization of all applications in the tier is > than the threshold (assuming its full CPU request is available), the replicate
				if (avgUtil >= scaleOutThreshold && tier.getApplications().size() < tier.getMaxSize()) {
					//create a new replica
					logger.debug("Adding Replica to ____");
					replicateActions.add(new ReplicateAction(tier.getVMDescription(), vmPlacementPolicy));
				} else if ((avgUtil * tier.getApplications().size()) / (tier.getApplications().size() - 1) < scaleInThreshold && tier.getApplications().size() > tier.getMinSize()) {
					//average utilization will still be below threshold if we remove a VM, so shut down the VM on the host with the fewest other VMs (hoping to shut down Host as well)
					logger.debug("Shutting down Replica for ____");
					
					VM targetVm = null;
					int nVms = Integer.MAX_VALUE;
					for (Application application : tier.getApplications()) {
						int hostVmCount = application.getVM().getVMAllocation().getHost().getVMAllocations().size(); 
						if(hostVmCount < nVms &&
								!application.getVM().getVMAllocation().getHost().getMigratingOut().contains(application.getVM())) {
							nVms = hostVmCount;
							targetVm = application.getVM();
						}
					}
					if (targetVm != null) {
						shutdownActions.add(new ShutdownVmAction(targetVm));
					} else {
						throw new RuntimeException("Service Replication could not find a VM to shut down for de-relication");
					}
				}
				
			}
		}
		
		for (ReplicateAction action : replicateActions) {
			action.execute(this);
		}
		
		for (ShutdownVmAction action : shutdownActions) {
			action.execute(this);
		}
	}

	@Override
	public long getNextExecutionTime() {
		return Simulation.getInstance().getSimulationTime() + interval;
	}

	@Override
	public void processEvent(Event e) {
		//nothing to do
	}

	
	
}
