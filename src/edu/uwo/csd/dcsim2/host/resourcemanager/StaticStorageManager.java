package edu.uwo.csd.dcsim2.host.resourcemanager;

import edu.uwo.csd.dcsim2.vm.*;

public class StaticStorageManager extends StorageManager {

	@Override
	public boolean isCapable(VMDescription vmDescription) {
		return vmDescription.getStorage() <= getTotalStorage();
	}

	@Override
	public boolean hasCapacity(VMAllocationRequest vmAllocationRequest) {
		return vmAllocationRequest.getStorageAllocation().getStorageAlloc() <= getAvailableStorage();
	}

	@Override
	public void allocateResource(VMAllocationRequest vmAllocationRequest,
			VMAllocation vmAllocation) {
		
		if (hasCapacity(vmAllocationRequest)) {
			StorageAllocation newAlloc = new StorageAllocation(vmAllocationRequest.getStorageAllocation().getStorageAlloc());
			vmAllocation.setStorageAllocation(newAlloc);
			allocationMap.put(vmAllocation, newAlloc);
		}
	}

	@Override
	public void deallocateResource(VMAllocation vmAllocation) {
		vmAllocation.setStorageAllocation(null);
		allocationMap.remove(vmAllocation);
	}

	@Override
	public void updateAllocations() {
		//do nothing, allocation is static
	}

	@Override
	public void allocatePrivDomain(VMAllocationRequest vmAllocationRequest,
			VMAllocation privDomainAllocation) {
		
		if (hasCapacity(vmAllocationRequest)) {
			StorageAllocation newAlloc = new StorageAllocation(vmAllocationRequest.getStorageAllocation().getStorageAlloc());
			privDomainAllocation.setStorageAllocation(newAlloc);
			this.privDomainAllocation = privDomainAllocation;
		}
	}

	
	
}
