package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.ArrayList;

import edu.uwo.csd.dcsim2.vm.*;

public class StaticCpuManager extends CpuManager {
	
	@Override
	public boolean isCapable(VMDescription vmDescription) {
		//check cores and core capacity
		if (vmDescription.getCores() * vmDescription.getCoreCapacity() > this.getTotalCpu())
			return false;
		
		return true;
	}

	@Override
	public boolean hasCapacity(VMAllocationRequest vmAllocationRequest) {
		return vmAllocationRequest.getCpuAllocation().getTotalAlloc() <= this.getAvailableAllocation();
	}

	@Override
	public boolean hasCapacity(
			ArrayList<VMAllocationRequest> vmAllocationRequests) {
		
		double totalAlloc = 0;
		
		for (VMAllocationRequest allocationRequest : vmAllocationRequests)
			totalAlloc += allocationRequest.getCpuAllocation().getTotalAlloc();
		
		return totalAlloc <= this.getAvailableAllocation();
	}
	
	@Override
	public boolean allocateResource(VMAllocationRequest vmAllocationRequest, VMAllocation vmAllocation) {

		if (hasCapacity(vmAllocationRequest)) {
			CpuAllocation newAlloc = new CpuAllocation();
			for (Integer coreCapacity : vmAllocationRequest.getCpuAllocation().getCoreAlloc()) {
				newAlloc.getCoreAlloc().add(coreCapacity);
			}
			vmAllocation.setCpuAllocation(newAlloc);
			allocationMap.put(vmAllocation, newAlloc);
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void allocatePrivDomain(VMAllocation privDomainAllocation) {

		if (this.getAvailableAllocation() >= 500) { //500 allows for 300 for the VMM and 200 for 2 migrations
			CpuAllocation newAlloc = new CpuAllocation(1, 500);
			privDomainAllocation.setCpuAllocation(newAlloc);
			setPrivDomainAllocation(privDomainAllocation);
		} else {
			throw new RuntimeException("Could not allocate privileged domain on Host #" + getHost().getId());
		}
	}

	@Override
	public void deallocateResource(VMAllocation vmAllocation) {
		vmAllocation.setCpuAllocation(null);
		allocationMap.remove(vmAllocation);
	}

	@Override
	public void updateAllocations() {
		//do nothing, allocation is static
	}

	

}
