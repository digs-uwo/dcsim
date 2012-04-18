package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.ArrayList;

import edu.uwo.csd.dcsim2.vm.*;

public class StaticCpuManager extends CpuManager {
	
	int privDomainAlloc;
	
	public StaticCpuManager(int privDomainAlloc) {
		this.privDomainAlloc = privDomainAlloc;
	}
	
	@Override
	public boolean isCapable(VMDescription vmDescription) {
		//check cores and core capacity
		if (vmDescription.getCores() > this.getHost().getCoreCount())
			return false;
		if (vmDescription.getCoreCapacity() > this.getHost().getMaxCoreCapacity())
			return false;
				
		return true;
	}

	@Override
	public boolean hasCapacity(VMAllocationRequest vmAllocationRequest) {
		return vmAllocationRequest.getCpu() <= this.getAvailableAllocation();
	}

	@Override
	public boolean hasCapacity(
			ArrayList<VMAllocationRequest> vmAllocationRequests) {
		
		double totalAlloc = 0;
		
		for (VMAllocationRequest allocationRequest : vmAllocationRequests)
			totalAlloc += allocationRequest.getCpu();
		
		return totalAlloc <= this.getAvailableAllocation();
	}
	
	@Override
	public boolean allocateResource(VMAllocationRequest vmAllocationRequest, VMAllocation vmAllocation) {

		if (hasCapacity(vmAllocationRequest)) {
			int newAlloc = vmAllocationRequest.getCpu();
			vmAllocation.setCpu(newAlloc);
			allocationMap.put(vmAllocation, newAlloc);
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void allocatePrivDomain(VMAllocation privDomainAllocation) {

		if (this.getAvailableAllocation() >= privDomainAlloc) {
			privDomainAllocation.setCpu(privDomainAlloc);
			setPrivDomainAllocation(privDomainAllocation);
		} else {
			throw new RuntimeException("Could not allocate privileged domain on Host #" + getHost().getId());
		}
	}

	@Override
	public void deallocateResource(VMAllocation vmAllocation) {
		vmAllocation.setCpu(0);
		allocationMap.remove(vmAllocation);
	}

	@Override
	public void updateAllocations() {
		//do nothing, allocation is static
	}

	

}
