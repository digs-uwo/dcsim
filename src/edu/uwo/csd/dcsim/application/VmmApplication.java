package edu.uwo.csd.dcsim.application;

import java.util.ArrayList;

import edu.uwo.csd.dcsim.common.Utility;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.vm.*;

/**
 * VmmApplication is a special application that handles the virtual machine manager on a Host.
 * @author Michael Tighe
 *
 */
public class VmmApplication extends Application {

	private ArrayList<VM> migratingVms = new ArrayList<VM>();
	private VirtualResources resourcesRemaining;
	
	protected double cpuOverhead = 300; //fixed cpu overhead
	protected double bandwidthOverhead = 0; //fixed bandwidth overhead
	
	//variables to keep track of resource demand and consumption
	VirtualResources resourceDemand = new VirtualResources();		//the current level of resource demand / second
	VirtualResources resourceInUse = new VirtualResources();			//the current level of resource use  / second
	
	VirtualResources resourcesDemanded = new VirtualResources(); 	//the amount of resources demanded in the last period
	VirtualResources resourcesUsed = new VirtualResources();			//the amount of resources used in the last period
	
	VirtualResources totalResourceDemand = new VirtualResources();	//the total amount of resources required since the application started
	VirtualResources totalResourceUsed = new VirtualResources();		//the total amount of resources used since the application started
	
	public VmmApplication(Simulation simulation) {
		super(simulation);
	}
	
	public void addMigratingVm(VM vm) {
		migratingVms.add(vm);
	}
	
	public void removeMigratingVm(VM vm) {
		migratingVms.remove(vm);
	}
	
	protected double getMigrationCpu(VM migratingVm) {
		return migratingVm.getResourcesInUse().getCpu() * 0.1;
	}
	
	protected double getMigrationBandwidth(VM migratingVm) {
		return 100; //TODO: add proper overhead calculation
	}
	
	@Override
	public void prepareExecution() {
		long elapsedTime = simulation.getElapsedTime();
		
		//reset the resource demand and consumption values for the current interval
		resourcesDemanded = new VirtualResources();
		resourcesUsed = new VirtualResources();
		
		double cpu = cpuOverhead;
		double bandwidth = bandwidthOverhead;
		for (VM migrating : migratingVms) {
			
			/*  Calculate CPU overhead as 10% of utilization over last 
			 *  elapsed period for each migrating VM. Note, it may be
			 *  more accurate to use 10% of current utilization, but this
			 *  is not possible due to the way VM CPU is scheduled in the
			 *  simulator. It is unlikely to have changed drastically since
			 *  the last elapsed period, so this should be sufficiently accurate
			 *  (especially given that 10% is merely an estimation in any case).
			 */
			cpu += getMigrationCpu(migrating);
			Utility.roundDouble(cpu); //round off double precision problems
			
			bandwidth += getMigrationBandwidth(migrating); 
		}
		//TODO calculate memory and storage usage
			
		resourcesRemaining = new VirtualResources();
		resourcesRemaining.setCpu(cpu * (elapsedTime / 1000d));
		resourcesRemaining.setBandwidth(bandwidth * (elapsedTime / 1000d));
		
		resourcesDemanded = resourcesDemanded.add(resourcesRemaining);
	}
	
	@Override
	public void updateResourceDemand() {
		//nothing to do
	}

	@Override
	public VirtualResources execute(VirtualResources resourcesAvailable) {
		VirtualResources resourcesConsumed = new VirtualResources();
		
		//consume cpu
		if (resourcesAvailable.getCpu() >= resourcesRemaining.getCpu()) {
			resourcesConsumed.setCpu(resourcesRemaining.getCpu());
			resourcesRemaining.setCpu(0);
		} else {
			resourcesConsumed.setCpu(resourcesAvailable.getCpu());
			resourcesRemaining.setCpu(resourcesRemaining.getCpu() - resourcesAvailable.getCpu());
		}

		//consume bandwidth
		if (resourcesAvailable.getBandwidth() >= resourcesRemaining.getBandwidth()) {
			resourcesConsumed.setBandwidth(resourcesRemaining.getBandwidth());
			resourcesRemaining.setBandwidth(0);
		} else {
			resourcesConsumed.setBandwidth(resourcesAvailable.getBandwidth());
			resourcesRemaining.setBandwidth(resourcesRemaining.getBandwidth() - resourcesAvailable.getBandwidth());
		}
		
		//TODO handle memory and storage
		
		//add resourcesConsumed to resourcesInUse, which is keeping track of all resources used during this time interval
		resourcesUsed = resourcesUsed.add(resourcesConsumed);
		
		return resourcesConsumed;
	}
	
	@Override
	public void completeExecution() {
		//at this point, no resources should be remaining to run
		if (resourcesRemaining.getCpu() != 0 || resourcesRemaining.getBandwidth() != 0) {
			throw new RuntimeException(simulation.getSimulationTime() + " - VMM was underallocated. CPU [" + resourcesRemaining.getCpu() + "], BW [" + resourcesRemaining.getBandwidth() + "], Migs[" + migratingVms.size() + "] Host #" + migratingVms.get(0).getVMAllocation().getHost().getId());
		}
		
		//convert resourceDemand and resourceInUse to a 'resource per second' value by dividing by seconds elapsed in time interval
		resourceDemand = new VirtualResources();
		resourceDemand.setCpu(resourcesDemanded.getCpu() / (simulation.getElapsedSeconds()));
		resourceDemand.setBandwidth(resourcesDemanded.getBandwidth() / (simulation.getElapsedSeconds()));
		resourceDemand.setMemory(resourcesDemanded.getMemory());
		resourceDemand.setStorage(resourcesDemanded.getStorage());
		
		resourceInUse = new VirtualResources();
		resourceInUse.setCpu(resourcesUsed.getCpu() / (simulation.getElapsedSeconds()));
		resourceInUse.setBandwidth(resourcesUsed.getBandwidth() / (simulation.getElapsedSeconds()));
		resourceInUse.setMemory(resourcesUsed.getMemory());
		resourceInUse.setStorage(resourcesUsed.getStorage());
		
	}
	
	@Override
	public void updateMetrics() {
		//add resource demand and use for this time interval to total values
		totalResourceDemand = totalResourceDemand.add(resourcesDemanded);
		totalResourceUsed = totalResourceUsed.add(resourcesUsed);
		
		//note that we do NOT add VMM demand and use to the global metrics, as we are only interested in demand from client VMs
	}


	@Override
	public VirtualResources getResourceDemand() {
		return resourceDemand;
	}

	@Override
	public VirtualResources getResourceInUse() {
		return resourceInUse;
	}

	@Override
	public VirtualResources getTotalResourceDemand() {
		return totalResourceDemand;
	}

	@Override
	public VirtualResources getTotalResourceUsed() {
		return totalResourceUsed;
	}

	/*
	 * SLA Methods
	 * All return 0, as the VMM has no SLA
	 */
	
	@Override
	public double getSLAViolation() {
		return 0;
	}

	@Override
	public double getTotalSLAViolation() {
		return 0;
	}

	@Override
	public double getSLAViolatedWork() {
		return 0;
	}

	@Override
	public double getTotalSLAViolatedWork() {
		return 0;
	}
	
	@Override
	public double getMigrationPenalty() {
		return 0;
	}
	
	@Override
	public double getTotalMigrationPenalty() {
		return 0;
	}

	@Override
	public double getIncomingWork() {
		return 0;
	}

	@Override
	public double getTotalIncomingWork() {
		return 0;
	}	

	//I dont know what to do here
	public double getWork(){
		return 0;
	}
}
