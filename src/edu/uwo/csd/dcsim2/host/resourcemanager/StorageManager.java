package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.HashMap;
import java.util.Map;

import edu.uwo.csd.dcsim2.vm.VMAllocation;

public abstract class StorageManager extends ResourceManager {

	Map<VMAllocation, Long> allocationMap = new HashMap<VMAllocation, Long>();
	VMAllocation privDomainAllocation;

	public long getTotalStorage() {
		return getHost().getStorage();
	}
	
	public long getAllocatedStorage() {
		long storage = 0;
		
		if (privDomainAllocation != null)
			storage += privDomainAllocation.getStorage();
		
		for (Long alloc : allocationMap.values()) {
			storage += alloc;
		}
		return storage;
	}
	
	public long getAvailableStorage() {
		return getTotalStorage() - getAllocatedStorage();
	}

}
