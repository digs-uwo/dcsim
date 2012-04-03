package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.HashMap;
import java.util.Map;

import edu.uwo.csd.dcsim2.vm.VMAllocation;

public abstract class BandwidthManager extends ResourceManager {
	
	Map<VMAllocation, Integer> allocationMap = new HashMap<VMAllocation, Integer>();
	VMAllocation privDomainAllocation;
	
	public int getTotalBandwidth() {
		return getHost().getBandwidth();
	}
	
	public int getAllocatedBandwidth() {
		int bandwidth = 0;
		
		if (privDomainAllocation != null) {
			bandwidth += privDomainAllocation.getBandwidth();
		}
		
		for (int allocation : allocationMap.values()) {
			bandwidth += allocation;
		}
		return bandwidth;
	}
	
	public int getAvailableBandwidth() {
		return getTotalBandwidth() - getAllocatedBandwidth();
	}
	
}
