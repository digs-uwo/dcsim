package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.HashMap;
import java.util.Map;

import edu.uwo.csd.dcsim2.vm.StorageAllocation;
import edu.uwo.csd.dcsim2.vm.VMAllocation;

public abstract class StorageManager extends ResourceManager {

	Map<VMAllocation, StorageAllocation> allocationMap = new HashMap<VMAllocation, StorageAllocation>();
	VMAllocation privDomainAllocation;

	public long getTotalStorage() {
		return getHost().getStorage();
	}
	
	public long getAllocatedStorage() {
		long storage = 0;
		
		if (privDomainAllocation != null)
			storage += privDomainAllocation.getStorageAllocation().getStorageAlloc();
		
		for (StorageAllocation alloc : allocationMap.values()) {
			storage += alloc.getStorageAlloc();
		}
		return storage;
	}
	
	public long getAvailableStorage() {
		return getTotalStorage() - getAllocatedStorage();
	}

}
