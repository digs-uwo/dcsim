package edu.uwo.csd.dcsim2.vm;

public class StorageAllocation {
	
	private long storageAlloc;
	
	public StorageAllocation() {
		storageAlloc = 0;
	}
	
	public StorageAllocation(long storageAlloc) {
		this.storageAlloc = storageAlloc;
	}
	
	public long getStorageAlloc() {
		return storageAlloc;
	}
	
	public void setStorageAlloc(long storageAlloc) {
		this.storageAlloc = storageAlloc;
	}
}
