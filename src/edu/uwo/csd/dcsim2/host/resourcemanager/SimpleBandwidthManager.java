package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.Collection;

import edu.uwo.csd.dcsim2.vm.*;

/**
 * SimpleBandwidthManager allocates bandwidth to VMs provided there is enough bandwidth available to satisfy the entire requested amount
 * 
 * @author Michael Tighe
 *
 */
public class SimpleBandwidthManager extends BandwidthManager {
	
	@Override
	public boolean isCapable(VMDescription vmDescription) {
		return vmDescription.getBandwidth() <= getTotalBandwidth();
	}

	@Override
	public boolean hasCapacity(int bandwidth) {
		return bandwidth <= getAvailableBandwidth();
	}
	
	@Override
	public boolean hasCapacity(VMAllocationRequest vmAllocationRequest) {
		return vmAllocationRequest.getBandwidth() <= getAvailableBandwidth();
	}
	
	@Override
	public boolean hasCapacity(Collection<VMAllocationRequest> vmAllocationRequests) {
		
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
			
			return true;
		}
		
		return false;
	}

	@Override
	public void deallocateResource(VMAllocation vmAllocation) {
		vmAllocation.setBandwidth(0);
	}

	@Override
	public void allocatePrivDomain(VMAllocation privDomainAllocation, int allocation) {
		if (hasCapacity(allocation)) {
			privDomainAllocation.setBandwidth(allocation);
		}
	}






}
