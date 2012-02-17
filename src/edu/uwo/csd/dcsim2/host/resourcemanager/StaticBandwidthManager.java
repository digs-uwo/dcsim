package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.*;

import edu.uwo.csd.dcsim2.vm.*;

public class StaticBandwidthManager extends BandwidthManager {

	Map<VMAllocation, BandwidthAllocation> allocationMap;
	
	public StaticBandwidthManager() {
		allocationMap = new HashMap<VMAllocation, BandwidthAllocation>();
	}
	
	private int getTotalBandwidth() {
		return getHost().getBandwidth();
	}
	
	private int getAllocatedBandwidth() {
		int bandwidth = 0;
		for (BandwidthAllocation allocation : allocationMap.values()) {
			bandwidth += allocation.getBandwidthAlloc();
		}
		return bandwidth;
	}
	
	public int getAvailableBandwidth() {
		return getTotalBandwidth() - getAllocatedBandwidth();
	}
	
	@Override
	public boolean isCapable(VMDescription vmDescription) {
		return vmDescription.getBandwidth() <= getTotalBandwidth();
	}

	@Override
	public boolean hasCapacity(VMAllocationRequest vmAllocationRequest) {
		return vmAllocationRequest.getBandwidthAllocation().getBandwidthAlloc() <= getAvailableBandwidth();
	}

	@Override
	public void allocateResource(VMAllocationRequest vmAllocationRequest,
			VMAllocation vmAllocation) {
		
		if (hasCapacity(vmAllocationRequest)) {
			BandwidthAllocation newAlloc = new BandwidthAllocation(vmAllocationRequest.getBandwidthAllocation().getBandwidthAlloc());
			vmAllocation.setBandwidthAllocation(newAlloc);
			allocationMap.put(vmAllocation, newAlloc);
		}
		
	}

	@Override
	public void deallocateResource(VMAllocation vmAllocation) {
		vmAllocation.setBandwidthAllocation(null);
		allocationMap.remove(vmAllocation);
	}

	@Override
	public void updateAllocations() {
		//do nothing, allocation is static
	}

}
