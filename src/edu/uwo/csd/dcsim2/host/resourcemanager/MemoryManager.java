package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.HashMap;
import java.util.Map;

import edu.uwo.csd.dcsim2.vm.MemoryAllocation;
import edu.uwo.csd.dcsim2.vm.VMAllocation;

public abstract class MemoryManager extends ResourceManager {
	
	Map<VMAllocation, MemoryAllocation> allocationMap = new HashMap<VMAllocation, MemoryAllocation>();
	VMAllocation privDomainAllocation;
	
	public int getAllocatedMemory() {
		int memory = 0;
		
		if (privDomainAllocation != null) {
			memory += privDomainAllocation.getMemoryAllocation().getMemoryAlloc();
		}
		
		for (MemoryAllocation memAlloc : allocationMap.values()) {
			memory += memAlloc.getMemoryAlloc();
		}
		return memory;
	}
	
	public int getAvailableMemory() {
		return getTotalMemory() - getAllocatedMemory();
	}
	
	public int getTotalMemory() {
		return getHost().getMemory();
	}
}
