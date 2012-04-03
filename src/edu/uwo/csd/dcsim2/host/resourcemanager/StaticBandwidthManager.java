package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.ArrayList;

import edu.uwo.csd.dcsim2.vm.*;

public class StaticBandwidthManager extends BandwidthManager {
	
	int privDomainAlloc;
	
	public StaticBandwidthManager(int privDomainAlloc) {
		this.privDomainAlloc = privDomainAlloc;
	}
	
	@Override
	public boolean isCapable(VMDescription vmDescription) {
		return vmDescription.getBandwidth() <= getTotalBandwidth();
	}

	@Override
	public boolean hasCapacity(VMAllocationRequest vmAllocationRequest) {
		return vmAllocationRequest.getBandwidth() <= getAvailableBandwidth();
	}
	
	@Override
	public boolean hasCapacity(
			ArrayList<VMAllocationRequest> vmAllocationRequests) {
		
		int totalAlloc = 0;
		
		for (VMAllocationRequest allocationRequest : vmAllocationRequests)
			totalAlloc += allocationRequest.getBandwidth();
		
		return totalAlloc <= getAvailableBandwidth();
	}

	@Override
	public boolean allocateResource(VMAllocationRequest vmAllocationRequest,
			VMAllocation vmAllocation) {
		
		if (hasCapacity(vmAllocationRequest)) {
			int newAlloc = vmAllocationRequest.getBandwidth();
			vmAllocation.setBandwidth(newAlloc);
			allocationMap.put(vmAllocation, newAlloc);
			
			return true;
		}
		
		return false;
	}

	@Override
	public void deallocateResource(VMAllocation vmAllocation) {
		vmAllocation.setBandwidth(0);
		allocationMap.remove(vmAllocation);
	}

	@Override
	public void updateAllocations() {
		//do nothing, allocation is static
	}

	@Override
	public void allocatePrivDomain(VMAllocation privDomainAllocation) {
		
		if (getAvailableBandwidth() >= privDomainAlloc) {
			privDomainAllocation.setBandwidth(privDomainAlloc);
			this.privDomainAllocation = privDomainAllocation;
		}
	}




}
