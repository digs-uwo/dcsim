package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.HashMap;
import java.util.Map;

import edu.uwo.csd.dcsim2.vm.BandwidthAllocation;
import edu.uwo.csd.dcsim2.vm.VMAllocation;

public abstract class BandwidthManager extends ResourceManager {
	
	Map<VMAllocation, BandwidthAllocation> allocationMap = new HashMap<VMAllocation, BandwidthAllocation>();
	VMAllocation privDomainAllocation;
	
	public int getTotalBandwidth() {
		return getHost().getBandwidth();
	}
	
	public int getAllocatedBandwidth() {
		int bandwidth = 0;
		
		if (privDomainAllocation != null) {
			bandwidth += privDomainAllocation.getBandwidthAllocation().getBandwidthAlloc();
		}
		
		for (BandwidthAllocation allocation : allocationMap.values()) {
			bandwidth += allocation.getBandwidthAlloc();
		}
		return bandwidth;
	}
	
	public int getAvailableBandwidth() {
		return getTotalBandwidth() - getAllocatedBandwidth();
	}
	
}
