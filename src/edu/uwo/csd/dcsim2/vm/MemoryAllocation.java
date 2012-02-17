package edu.uwo.csd.dcsim2.vm;

public class MemoryAllocation {

	private int memoryAlloc;
	
	public MemoryAllocation() {
		memoryAlloc = 0;
	}
	
	public MemoryAllocation(int memoryAlloc) {
		this.memoryAlloc = memoryAlloc;
	}
	
	public int getMemoryAlloc() {
		return memoryAlloc;
	}
	
	public void setMemoryAlloc(int memoryAlloc) {
		this.memoryAlloc = memoryAlloc;
	}
	
}
