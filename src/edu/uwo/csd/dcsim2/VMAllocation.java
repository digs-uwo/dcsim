package edu.uwo.csd.dcsim2;

public class VMAllocation {

	VM vm;
	
	private int cpuAlloc //array? list? how should this be done? Map<coreID, alloc>?
	private int memoryAlloc;	
	private int bandwidthAlloc;
	private long storageAlloc;
	
	public VMAllocation() {
			
		vm = null;
	}
	
	public void setVm(VM vm) {
		this.vm = vm;
	}
	
	public VM getVm() {
		return vm;
	}
	
	public void setMemoryAlloc(int memoryAlloc) {
		this.memoryAlloc = memoryAlloc;
	}
	
	public int getMemoryAlloc() {
		return memoryAlloc;
	}
	
	public void setBandwidthAlloc(int bandwidthAlloc) {
		this.bandwidthAlloc = bandwidthAlloc;
	}
	
	public int getBandwidthAlloc() {
		return bandwidthAlloc;
	}
	
	public void setStorageAlloc(long storageAlloc) {
		this.storageAlloc = storageAlloc;
	}
	
	public long getStorageAlloc() {
		return storageAlloc;
	}
	
}
