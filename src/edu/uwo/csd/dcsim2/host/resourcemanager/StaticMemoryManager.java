package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.ArrayList;

import edu.uwo.csd.dcsim2.vm.*;

public class StaticMemoryManager extends MemoryManager {

	@Override
	public boolean isCapable(VMDescription vmDescription) {
		return vmDescription.getMemory() <= getTotalMemory();
	}

	@Override
	public boolean hasCapacity(VMAllocationRequest vmAllocationRequest) {
		return vmAllocationRequest.getMemory() <= getAvailableMemory();
	}
	
	@Override
	public boolean hasCapacity(
			ArrayList<VMAllocationRequest> vmAllocationRequests) {

		int totalAlloc = 0;
		
		for (VMAllocationRequest allocationRequest : vmAllocationRequests)
			totalAlloc += allocationRequest.getMemory();

		return totalAlloc <= getAvailableMemory();
	}

	@Override
	public boolean allocateResource(VMAllocationRequest vmAllocationRequest,
			VMAllocation vmAllocation) {

		if (hasCapacity(vmAllocationRequest)) {
			int newAlloc = vmAllocationRequest.getMemory();
			vmAllocation.setMemory(newAlloc);
			allocationMap.put(vmAllocation, newAlloc);
			
			return true;
		}
		
		return false;
	}

	@Override
	public void deallocateResource(VMAllocation vmAllocation) {
		vmAllocation.setMemory(0);
		allocationMap.remove(vmAllocation);
	}

	@Override
	public void updateAllocations() {
		//do nothing, allocation is static
	}

	@Override
	public void allocatePrivDomain(VMAllocation privDomainAllocation) {
		privDomainAllocation.setMemory(0); //currently allocating no memory
	}

	

	
	
}
