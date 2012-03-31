package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.ArrayList;

import edu.uwo.csd.dcsim2.vm.*;

public class StaticBandwidthManager extends BandwidthManager {
	
	@Override
	public boolean isCapable(VMDescription vmDescription) {
		return vmDescription.getBandwidth() <= getTotalBandwidth();
	}

	@Override
	public boolean hasCapacity(VMAllocationRequest vmAllocationRequest) {
		return vmAllocationRequest.getBandwidthAllocation().getBandwidthAlloc() <= getAvailableBandwidth();
	}
	
	@Override
	public boolean hasCapacity(
			ArrayList<VMAllocationRequest> vmAllocationRequests) {
		
		int totalAlloc = 0;
		
		for (VMAllocationRequest allocationRequest : vmAllocationRequests)
			totalAlloc += allocationRequest.getBandwidthAllocation().getBandwidthAlloc();
		
		return totalAlloc <= getAvailableBandwidth();
	}

	@Override
	public boolean allocateResource(VMAllocationRequest vmAllocationRequest,
			VMAllocation vmAllocation) {
		
		if (hasCapacity(vmAllocationRequest)) {
			BandwidthAllocation newAlloc = new BandwidthAllocation(vmAllocationRequest.getBandwidthAllocation().getBandwidthAlloc());
			vmAllocation.setBandwidthAllocation(newAlloc);
			allocationMap.put(vmAllocation, newAlloc);
			
			return true;
		}
		
		return false;
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

	@Override
	public void allocatePrivDomain(VMAllocation privDomainAllocation) {
		
		if (getAvailableBandwidth() >= 200) {
			BandwidthAllocation newAlloc = new BandwidthAllocation(200);
			privDomainAllocation.setBandwidthAllocation(newAlloc);
			this.privDomainAllocation = privDomainAllocation;
		}
	}




}
