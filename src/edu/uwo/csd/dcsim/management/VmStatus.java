package edu.uwo.csd.dcsim.management;

import edu.uwo.csd.dcsim.host.Resources;
import edu.uwo.csd.dcsim.vm.Vm;

public class VmStatus {
	
	long timeStamp;
	int id = 0;
	int cores;
	int coreCapacity;
	Resources resourcesInUse;
	Vm vm;
	
	public VmStatus(Vm vm, long timeStamp){
		this.timeStamp = timeStamp;
		this.vm = vm;
		
		id = vm.getId();
		cores = vm.getVMDescription().getCores();
		coreCapacity = vm.getVMDescription().getCoreCapacity();
		resourcesInUse = vm.getResourcesScheduled().copy();
	}
	
	public VmStatus(VmStatus vmStatus) {
		timeStamp = vmStatus.timeStamp;
		vm = vmStatus.getVm();
		id = vmStatus.id;
		cores = vmStatus.cores;
		coreCapacity = vmStatus.coreCapacity;
		resourcesInUse = vmStatus.resourcesInUse.copy();
	}
	
	/**
	 * Creates a "dummy" VM status for a placeholder
	 * @param cores
	 * @param coreCapacity
	 * @param resources
	 */
	public VmStatus(int cores, int coreCapacity, Resources resources) {
		this.id = -1;
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
	
	public Vm getVm() {
		return vm;
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
