package edu.uwo.csd.dcsim.vm;

/**
 * A request sent to a Host asking it to create a VMAllocation to host a VM, with specified properties.
 * 
 * @author Michael Tighe
 *
 */
public class VMAllocationRequest {

	private VMDescription vmDescription;
	private int cpu;
	private int memory;
	private int bandwidth;
	private long storage;

	public VMAllocationRequest(VMDescription vmDescription,
			int cpu, 
			int memory, 
			int bandwidth, 
			long storage) {
		
		this.vmDescription = vmDescription;
		this.cpu = cpu;
		this.memory = memory;
		this.bandwidth = bandwidth;
		this.storage = storage;
	}
	
	public VMAllocationRequest(VMAllocation vmAllocation) {
		vmDescription = vmAllocation.getVMDescription();
		
		cpu = vmAllocation.getCpu();
		memory = vmAllocation.getMemory();
		bandwidth = vmAllocation.getBandwidth();
		storage = vmAllocation.getStorage();	
	}
	
	public VMAllocationRequest(VMDescription vmDescription) {
		this.vmDescription =  vmDescription;
		
		cpu = vmDescription.getCores() * vmDescription.getCoreCapacity();
		memory = vmDescription.getMemory();
		bandwidth = vmDescription.getBandwidth();
		storage = vmDescription.getStorage();
	}
	
	public VMDescription getVMDescription() {
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
	
	public long getStorage() {
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
	
	public void setStorage(long storage) {
		this.storage = storage;
	}
	
}
