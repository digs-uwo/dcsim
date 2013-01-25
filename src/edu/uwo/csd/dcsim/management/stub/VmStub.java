package edu.uwo.csd.dcsim.management.stub;

import edu.uwo.csd.dcsim.vm.*;

public class VmStub {

	private VM vm;
	private HostStub host;
	
	public VmStub(VM vm) {
		this.vm = vm;
	}
	
	public VmStub(VM vm, HostStub host) {
		this(vm);
		this.host = host;
	}
	
	public HostStub getHost() {
		return host;
	}
	
	public void setHost(HostStub host) {
		this.host = host;
	}
	
	public double getCpuInUse() {
		return vm.getResourcesScheduled().getCpu();
	}
	
	public double getCpuAlloc() {
		return vm.getVMAllocation().getCpu();
	}

	public double getBandwidthInUse() {
		return vm.getResourcesScheduled().getBandwidth();
	}
	
	public int getBandwidthAlloc() {
		return vm.getVMAllocation().getBandwidth();
	}
	
	public int getMemoryInUse() {
		return vm.getResourcesScheduled().getMemory();
	}
	
	public int getMemoryAlloc() {
		return vm.getVMAllocation().getMemory();
	}
	
	public long getStorageInUse() {
		return vm.getResourcesScheduled().getStorage();
	}
	
	public long getStorageAlloc() {
		return vm.getVMAllocation().getStorage();
	}
	
	public VM getVM() {
		return vm;
	}
	
}
