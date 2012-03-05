package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.*;

import edu.uwo.csd.dcsim2.vm.*;

public class StaticMemoryManager extends MemoryManager {

	Map<VMAllocation, MemoryAllocation> allocationMap;
	VMAllocation privDomainAllocation;
	
	public StaticMemoryManager() {
		allocationMap = new HashMap<VMAllocation, MemoryAllocation>();
	}
	
	private int getAllocatedMemory() {
		int memory = 0;
		
		if (privDomainAllocation != null) {
			memory += privDomainAllocation.getMemoryAllocation().getMemoryAlloc();
		}
		
		for (MemoryAllocation memAlloc : allocationMap.values()) {
			memory += memAlloc.getMemoryAlloc();
		}
		return memory;
	}
	
	private int getAvailableMemory() {
		return getTotalMemory() - getAllocatedMemory();
	}
	
	private int getTotalMemory() {
		return getHost().getMemory();
	}
	
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
	public void allocatePrivDomain(VMAllocationRequest vmAllocationRequest,
			VMAllocation privDomainAllocation) {
		if (hasCapacity(vmAllocationRequest)) {
			MemoryAllocation newAlloc = new MemoryAllocation(vmAllocationRequest.getMemoryAllocation().getMemoryAlloc());
			privDomainAllocation.setMemoryAllocation(newAlloc);
			this.privDomainAllocation = privDomainAllocation;
		}
	}

	
	
}
