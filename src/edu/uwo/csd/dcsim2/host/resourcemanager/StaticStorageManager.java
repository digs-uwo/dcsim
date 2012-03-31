package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.ArrayList;

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
	public boolean hasCapacity(
			ArrayList<VMAllocationRequest> vmAllocationRequests) {

		long totalAlloc = 0;
		
		for (VMAllocationRequest allocationRequest : vmAllocationRequests)
			totalAlloc += allocationRequest.getStorageAllocation().getStorageAlloc();

		return totalAlloc <= getAvailableStorage();
	}

	@Override
	public boolean allocateResource(VMAllocationRequest vmAllocationRequest,
			VMAllocation vmAllocation) {
		
		if (hasCapacity(vmAllocationRequest)) {
			StorageAllocation newAlloc = new StorageAllocation(vmAllocationRequest.getStorageAllocation().getStorageAlloc());
			vmAllocation.setStorageAllocation(newAlloc);
			allocationMap.put(vmAllocation, newAlloc);
			
			return true;
		}
		
		return false;
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
	public void allocatePrivDomain(VMAllocation privDomainAllocation) {
		
		StorageAllocation newAlloc = new StorageAllocation(0); //currently allocating no storage
		privDomainAllocation.setStorageAllocation(newAlloc);
		this.privDomainAllocation = privDomainAllocation;
	}



	
	
}
