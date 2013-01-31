package edu.uwo.csd.dcsim.application;

import java.util.ArrayList;

import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.host.Resources;
import edu.uwo.csd.dcsim.vm.*;

/**
 * VmmApplication is a special application that handles the virtual machine manager on a Host.
 * @author Michael Tighe
 *
 */
public class VmmApplication extends Application {

	private ArrayList<VM> migratingVms = new ArrayList<VM>();
	private Resources resourcesScheduled = new Resources();
	
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
	public Resources calculateResourcesRequired() {
		Resources resourcesRequired = new Resources();
		resourcesRequired.setMemory(0);
		resourcesRequired.setStorage(0);
		
		double cpuRequired = cpuOverhead;
		double bandwidthRequired = 0;
		
		//calculate cpu and bandwidth requirements for migrating VMs
		//TODO this needs to consider the number of migrating VMs and the bandwidth available on the management network link
		for (VM migrating : migratingVms) {
			cpuRequired += migrating.getResourcesScheduled().getCpu() * 0.1; //TODO is there something more accurate than this?
			bandwidthRequired += 100; //TODO depends on available bandwidth?
		}
		
		resourcesRequired.setCpu(cpuRequired);
		resourcesRequired.setBandwidth(bandwidthRequired);
		
		return resourcesRequired;
	}

	@Override
	public void scheduleResources(Resources resourcesScheduled) {
		// TODO not sure if we need to do anything here...
		this.resourcesScheduled = resourcesScheduled;
	}
	
	@Override
	public void postScheduling() {
		//TODO this is where we will calculate VM migration times based on available bandwidth and trigger/update migration completion events
	}

	@Override
	public void execute() {
		//ensure that we have enough resources... for now, halt the simulation if insufficient resources were scheduled
		//TODO is there anything else to do here?
		Resources resourcesRequired = getResourcesRequired();
		
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
		return 0;
	}

	@Override
	public double getTotalSLAViolatedWork() {
		return 0;
	}

	@Override
	public Resources getResourcesInUse() {
		return resourcesScheduled;
	}

	@Override
	public double getSLAUnderprovisionRate() {
		return 0;
	}

	@Override
	public double getSLAMigrationPenaltyRate() {
		return 0;
	}

}
