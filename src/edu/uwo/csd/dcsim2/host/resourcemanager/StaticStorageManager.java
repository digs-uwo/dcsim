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
		return vmAllocationRequest.getStorage() <= getAvailableStorage();
	}
	
	@Override
	public boolean hasCapacity(
			ArrayList<VMAllocationRequest> vmAllocationRequests) {

		long totalAlloc = 0;
		
		for (VMAllocationRequest allocationRequest : vmAllocationRequests)
			totalAlloc += allocationRequest.getStorage();

		return totalAlloc <= getAvailableStorage();
	}

	@Override
	public boolean allocateResource(VMAllocationRequest vmAllocationRequest,
			VMAllocation vmAllocation) {
		
		if (hasCapacity(vmAllocationRequest)) {
			long newAlloc = vmAllocationRequest.getStorage();
			vmAllocation.setStorage(newAlloc);
			allocationMap.put(vmAllocation, newAlloc);
			
			return true;
		}
		
		return false;
	}

	@Override
	public void deallocateResource(VMAllocation vmAllocation) {
		vmAllocation.setStorage(0);
		allocationMap.remove(vmAllocation);
	}

	@Override
	public void updateAllocations() {
		//do nothing, allocation is static
	}

	@Override
	public void allocatePrivDomain(VMAllocation privDomainAllocation) {
		privDomainAllocation.setStorage(0); //currently allocating no storage
		this.privDomainAllocation = privDomainAllocation;
	}



	
	
}
