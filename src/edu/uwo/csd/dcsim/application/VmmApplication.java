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
	private VirtualResources resourcesScheduled = new VirtualResources();
	
	protected double cpuOverhead = 300; //fixed cpu overhead
	
	public VmmApplication(Simulation simulation) {
		super(simulation);
	}
	
	public void addMigratingVm(VM vm) {
		migratingVms.add(vm);
	}
	
	public void removeMigratingVm(VM vm) {
		migratingVms.remove(vm);
	}

	
	@Override
	public VirtualResources calculateResourcesRequired() {
		VirtualResources resourcesRequired = new VirtualResources();
		resourcesRequired.setMemory(0);
		resourcesRequired.setStorage(0);
		
		double cpuRequired = cpuOverhead;
		double bandwidthRequired = 0;
		
		//calculate cpu and bandwidth requirements for migrating VMs
		//TODO this needs to consider the number of migrating VMs and the bandwidth available on the management network link
		for (VM migrating : migratingVms) {
			cpuRequired += 100; //TODO change to percentage of VM CPU utilization?
			bandwidthRequired += 100; //TODO depends on available bandwidth?
		}
		
		resourcesRequired.setCpu(cpuRequired);
		resourcesRequired.setBandwidth(bandwidthRequired);
		
		return resourcesRequired;
	}

	@Override
	public void scheduleResources(VirtualResources resourcesScheduled) {
		// TODO not sure if we need to do anything here...
		this.resourcesScheduled = resourcesScheduled;
	}

	@Override
	public void execute() {
		//ensure that we have enough resources... for now, halt the simulation if insufficient resources were scheduled
		//TODO is there anything else to do here?
		VirtualResources resourcesRequired = getResourcesRequired();
		
		if (resourcesScheduled.getCpu() < resourcesRequired.getCpu()
				|| resourcesScheduled.getBandwidth() < resourcesRequired.getBandwidth()) {
			throw new RuntimeException(simulation.getSimulationTime() + " - VMM was underallocated. CPU [" + resourcesRequired.getCpu() + "], BW [" + resourcesRequired.getBandwidth() + "], Migs[" + migratingVms.size() + "] Host #" + migratingVms.get(0).getVMAllocation().getHost().getId());
		}
	}
	
	@Override
	public void updateMetrics() {

	}

	@Override
	public double getWorkOutputLevel() {
		// there is no work output
		return 0;
	}

	@Override
	public double getTotalIncomingWork() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTotalSLAViolatedWork() {
		// TODO Auto-generated method stub
		return 0;
	}

}
