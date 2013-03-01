package edu.uwo.csd.dcsim.management;

import edu.uwo.csd.dcsim.host.Resources;
import edu.uwo.csd.dcsim.vm.VM;

public class VmStatus {
	
	long timeStamp;
	int id;
	int cores;
	int coreCapacity;
	Resources resourcesInUse;
	
	public VmStatus(VM vm, long timeStamp){
		this.timeStamp = timeStamp;
		
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
	
	/**
	 * Creates a "dummy" VM status for a placeholder
	 * @param cores
	 * @param coreCapacity
	 * @param resources
	 */
	public VmStatus(int cores, int coreCapacity, Resources resources) {
		this.cores = cores;
		this.coreCapacity = coreCapacity;
		this.resourcesInUse = resources;
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
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof VmStatus) {
			VmStatus vm = (VmStatus)o;
			if (vm.getId() == id) {
				return true;
			}
		}
		return false;
	}
	
}
