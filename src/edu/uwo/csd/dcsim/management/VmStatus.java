package edu.uwo.csd.dcsim.management;

import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.host.Resources;
import edu.uwo.csd.dcsim.vm.VM;

public class VmStatus {
	
	long timeStamp;
	int id;
	int cores;
	int coreCapacity;
	Resources resourcesInUse;
	
	public VmStatus(VM vm, Simulation simulation){
		timeStamp = simulation.getSimulationTime();
		
		id = vm.getId();
		cores = vm.getVMDescription().getCores();
		coreCapacity = vm.getVMDescription().getCoreCapacity();
		resourcesInUse = vm.getResourcesScheduled().copy();
	}
	
	public VmStatus(VmStatus vm) {
		timeStamp = vm.timeStamp;
		id = vm.id;
		cores = vm.cores;
		coreCapacity = vm.coreCapacity;
		resourcesInUse = vm.resourcesInUse.copy();
	}
	
	public Resources getResourcesInUse() {
		return resourcesInUse;
	}
	
	public long getTimeStamp() {
		return timeStamp;
	}
	
	public int getId() {
		return id;
	}
	
	public int getCores() {
		return cores;
	}
	
	public int getCoreCapacity() {
		return coreCapacity;
	}
	
	public VmStatus copy() {
		return new VmStatus(this);
	}
	
}
