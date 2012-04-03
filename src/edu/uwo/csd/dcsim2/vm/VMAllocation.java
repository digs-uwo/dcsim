package edu.uwo.csd.dcsim2.vm;

import edu.uwo.csd.dcsim2.host.*;

public class VMAllocation {

	private VM vm;
	private VMDescription vmDescription;
	private Host host;
	private int cpu;
	private int memory;
	private int bandwidth;
	private long storage;
	
	public VMAllocation(VMDescription vmDescription, Host host) {
		this.vmDescription = vmDescription;
		this.host = host;
		vm = null;
	}
	
	public VirtualResources getResourcesInUse() {
		if (vm != null) {
			return vm.getResourcesInUse();
		} else {
			return new VirtualResources(); //if no VM, return new VirtualResources indicating 0 resources in use
		}
	}
	
	public void attachVm(VM vm) {
		this.vm = vm;
		vm.setVMAllocation(this);
	}
	
	public void setVm(VM vm) {
		this.vm = vm;
	}
	
	public VM getVm() {
		return vm;
	}
	
	public Host getHost() {
		return host;
	}
	
	public VMDescription getVMDescription() {
		return vmDescription;
	}
	
	public int getCpu() {
		return cpu;
	}
	
	public void setCpu(int cpu) {
		this.cpu = cpu;
	}
	
	public int getMemory() {
		return memory;
	}
	
	public void setMemory(int memory) {
		this.memory = memory;
	}
	
	public int getBandwidth() {
		return bandwidth;
	}
	
	public void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}
	
	public long getStorage() {
		return storage;
	}
	
	public void setStorage(long storage) {
		this.storage = storage;
	}

}
