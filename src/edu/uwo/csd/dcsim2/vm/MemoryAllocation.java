package edu.uwo.csd.dcsim2.vm;

public class MemoryAllocation {

	private VMAllocation vmAllocation;
	private int memoryAlloc;
	
	public MemoryAllocation(VMAllocation vmAllocation) {
		this.vmAllocation = vmAllocation;
	}
	
	public int getMemoryAlloc() {
		return memoryAlloc;
	}
	
	public void setMemoryAlloc(int memoryAlloc) {
		this.memoryAlloc = memoryAlloc;
	}
	
	public VMAllocation getVMAllocation() {
		return vmAllocation;
	}
	
}
