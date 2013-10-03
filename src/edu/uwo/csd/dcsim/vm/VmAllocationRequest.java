package edu.uwo.csd.dcsim.vm;

import edu.uwo.csd.dcsim.host.Resources;

/**
 * A request sent to a Host asking it to create a VMAllocation to host a VM, with specified properties.
 * 
 * @author Michael Tighe
 *
 */
public class VmAllocationRequest {

	private VmDescription vmDescription;
	private int cpu;
	private int memory;
	private int bandwidth;
	private int storage;

	public VmAllocationRequest(VmDescription vmDescription,
			int cpu, 
			int memory, 
			int bandwidth, 
			int storage) {
		
		this.vmDescription = vmDescription;
		this.cpu = cpu;
		this.memory = memory;
		this.bandwidth = bandwidth;
		this.storage = storage;
	}
	
	public VmAllocationRequest(VmAllocation vmAllocation) {
		vmDescription = vmAllocation.getVMDescription();
		
		cpu = vmAllocation.getCpu();
		memory = vmAllocation.getMemory();
		bandwidth = vmAllocation.getBandwidth();
		storage = vmAllocation.getStorage();	
	}
	
	public VmAllocationRequest(VmDescription vmDescription) {
		this.vmDescription =  vmDescription;
		
		cpu = vmDescription.getCores() * vmDescription.getCoreCapacity();
		memory = vmDescription.getMemory();
		bandwidth = vmDescription.getBandwidth();
		storage = vmDescription.getStorage();
	}
	
	public VmDescription getVMDescription() {
		return vmDescription;
	}

	public int getCpu() {
		return cpu;
	}
	
	public int getMemory() {
		return memory;
	}
	
	public int getBandwidth() {
		return bandwidth;
	}
	
	public int getStorage() {
		return storage;
	}
	
	public void setCpu(int cpu) {
		this.cpu = cpu;
	}
	
	public void setMemory(int memory) {
		this.memory = memory;
	}
	
	public void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}
	
	public void setStorage(int storage) {
		this.storage = storage;
	}
	
	public Resources getResources() {
		return new Resources(cpu, memory, bandwidth, storage);
	}
	
}
