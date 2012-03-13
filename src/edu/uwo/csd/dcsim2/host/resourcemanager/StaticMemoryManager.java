package edu.uwo.csd.dcsim2.host.resourcemanager;

import edu.uwo.csd.dcsim2.vm.*;

public class StaticMemoryManager extends MemoryManager {

	@Override
	public boolean isCapable(VMDescription vmDescription) {
		return vmDescription.getMemory() <= getTotalMemory();
	}

	@Override
	public boolean hasCapacity(VMAllocationRequest vmAllocationRequest) {
		return vmAllocationRequest.getMemoryAllocation().getMemoryAlloc() <= getAvailableMemory();
	}

	@Override
	public void allocateResource(VMAllocationRequest vmAllocationRequest,
			VMAllocation vmAllocation) {

		if (hasCapacity(vmAllocationRequest)) {
			MemoryAllocation newAlloc = new MemoryAllocation(vmAllocationRequest.getMemoryAllocation().getMemoryAlloc());
			vmAllocation.setMemoryAllocation(newAlloc);
			allocationMap.put(vmAllocation, newAlloc);
		}
		
	}

	@Override
	public void deallocateResource(VMAllocation vmAllocation) {
		vmAllocation.setMemoryAllocation(null);
		allocationMap.remove(vmAllocation);
	}

	@Override
	public void updateAllocations() {
		//do nothing, allocation is static
	}

	@Override
	public void allocatePrivDomain(VMAllocation privDomainAllocation) {
		MemoryAllocation newAlloc = new MemoryAllocation(0); //currently allocating no memory
		privDomainAllocation.setMemoryAllocation(newAlloc);
	}

	
	
}
