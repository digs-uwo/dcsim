package edu.uwo.csd.dcsim2.host.resourcemanager;

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

	@Override
	public void allocatePrivDomain(VMAllocationRequest vmAllocationRequest,
			VMAllocation privDomainAllocation) {
		
		if (hasCapacity(vmAllocationRequest)) {
			BandwidthAllocation newAlloc = new BandwidthAllocation(vmAllocationRequest.getBandwidthAllocation().getBandwidthAlloc());
			privDomainAllocation.setBandwidthAllocation(newAlloc);
			this.privDomainAllocation = privDomainAllocation;
		}
	}


}
