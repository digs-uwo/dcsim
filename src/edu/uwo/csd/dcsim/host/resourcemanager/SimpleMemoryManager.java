package edu.uwo.csd.dcsim.host.resourcemanager;

import java.util.Collection;

import edu.uwo.csd.dcsim.vm.*;

/**
 * SimpleMemoryManager allocates memory to VMs provided that there is enough unallocated memory to satisfy the entire requested allocation.
 * @author Michael Tighe
 *
 */
public class SimpleMemoryManager extends MemoryManager {

	@Override
	public boolean isCapable(VMDescription vmDescription) {
		return vmDescription.getMemory() <= getTotalMemory();
	}

	@Override
	public boolean hasCapacity(int memory) {
		return memory <= getAvailableMemory();
	}
		
	@Override
	public boolean hasCapacity(VMAllocationRequest vmAllocationRequest) {
		return hasCapacity(vmAllocationRequest.getMemory());
	}
	
	@Override
	public boolean hasCapacity(Collection<VMAllocationRequest> vmAllocationRequests) {
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
			
			return true;
		}
		
		return false;
	}

	@Override
	public void deallocateResource(VMAllocation vmAllocation) {
		vmAllocation.setMemory(0);
	}

	@Override
	public void allocatePrivDomain(VMAllocation privDomainAllocation, int allocation) {
		if (hasCapacity(allocation)) {
			privDomainAllocation.setMemory(allocation);
		} else {
			throw new RuntimeException("Could not allocate privileged domain on Host #" + getHost().getId());
		}
	}
	
}
