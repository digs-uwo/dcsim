package edu.uwo.csd.dcsim2.management;

import edu.uwo.csd.dcsim2.vm.*;

public class VmStub {

	private VM vm;
	
	public VmStub(VM vm) {
		this.vm = vm;
	}
	
	public double getCpuInUse() {
		return vm.getResourcesInUse().getCpu();
	}
	
	public double getCpuAlloc() {
		return vm.getVMAllocation().getCpu();
	}

	public double getBandwidthInUse() {
		return vm.getResourcesInUse().getBandwidth();
	}
	
	public int getBandwidthAlloc() {
		return vm.getVMAllocation().getBandwidth();
	}
	
	public int getMemoryInUse() {
		return vm.getResourcesInUse().getMemory();
	}
	
	public int getMemoryAlloc() {
		return vm.getVMAllocation().getMemory();
	}
	
	public long getStorageInUse() {
		return vm.getResourcesInUse().getStorage();
	}
	
	public long getStorageAlloc() {
		return vm.getVMAllocation().getStorage();
	}
	
	public VM getVM() {
		return vm;
	}
	
}
