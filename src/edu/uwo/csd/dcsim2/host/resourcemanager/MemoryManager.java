package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.HashMap;
import java.util.Map;

import edu.uwo.csd.dcsim2.vm.VMAllocation;

public abstract class MemoryManager extends ResourceManager {
	
	Map<VMAllocation, Integer> allocationMap = new HashMap<VMAllocation,Integer>();
	VMAllocation privDomainAllocation;
	
	public int getAllocatedMemory() {
		int memory = 0;
		
		if (privDomainAllocation != null) {
			memory += privDomainAllocation.getMemory();
		}
		
		for (Integer memAlloc : allocationMap.values()) {
			memory += memAlloc;
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
