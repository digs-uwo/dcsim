package edu.uwo.csd.dcsim.vm;

import edu.uwo.csd.dcsim.host.*;

/**
 * VMAllocation is an resource allocation made by a Host for a VM. VMAllocation is instantiate on a Host
 * to hold a VM, and never leaves the Host even if the VM migrates. A migrating VM moves to a new 
 * VMAllocation on the target Host. 
 * 
 * @author Michael Tighe
 *
 */
public class VmAllocation {

	private Vm vm;
	private VmDescription vmDescription;
	private Host host;
	private int cpu;
	private int memory;
	private int bandwidth;
	private int storage;
	
	public VmAllocation(VmDescription vmDescription, Host host) {
		this.vmDescription = vmDescription;
		this.host = host;
		vm = null;
	}
	
	public Resources getResourcesInUse() {
		if (vm != null) {
			//return vm.getResourcesInUse();
			return vm.getResourcesScheduled(); //TODO should there be a resourcesInUse? or is resourcesScheduled sufficient?
		} else {
			return new Resources(); //if no VM, return new VirtualResources indicating 0 resources in use
		}
	}
	
	public void attachVm(Vm vm) {
		this.vm = vm;
		vm.setVMAllocation(this);
	}
	
	public void setVm(Vm vm) {
		this.vm = vm;
	}
	
	public Vm getVm() {
		return vm;
	}
	
	public Host getHost() {
		return host;
	}
	
	public VmDescription getVMDescription() {
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
	
	public int getStorage() {
		return storage;
	}
	
	public void setStorage(int storage) {
		this.storage = storage;
	}

}
