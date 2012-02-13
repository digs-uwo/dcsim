package edu.uwo.csd.dcsim2.vm;

public class StorageAllocation {
	
	private VMAllocation vmAllocation;
	private int storageAlloc;
	
	public StorageAllocation(VMAllocation vmAllocation) {
		this.vmAllocation = vmAllocation;
	}
	
	public int getStorageAlloc() {
		return storageAlloc;
	}
	
	public void setStorageAlloc(int storageAlloc) {
		this.storageAlloc = storageAlloc;
	}
	
	public VMAllocation getVMAllocation() {
		return vmAllocation;
	}
}
