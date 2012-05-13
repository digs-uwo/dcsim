package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.Collection;

import edu.uwo.csd.dcsim2.vm.*;

/**
 * SimpleCpuManager allocates CPU to VMs on the Host provided there is enough available CPU to satisfy the entire requested allocation
 * 
 * @author Michael Tighe
 *
 */
public class SimpleCpuManager extends CpuManager {

	@Override
	public boolean hasCapacity(int cpu) {
		return cpu <= getAvailableAllocation();
	}
	
	@Override
	public boolean hasCapacity(VMAllocationRequest vmAllocationRequest) {
		return vmAllocationRequest.getCpu() <= this.getAvailableAllocation();
	}

	@Override
	public boolean hasCapacity(Collection<VMAllocationRequest> vmAllocationRequests) {
		
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

			return true;
		}
		
		return false;
	}
	
	@Override
	public void allocatePrivDomain(VMAllocation privDomainAllocation, int allocation) {

		if (hasCapacity(allocation)) {
			privDomainAllocation.setCpu(allocation);
		} else {
			throw new RuntimeException("Could not allocate privileged domain on Host #" + getHost().getId());
		}
	}

	@Override
	public void deallocateResource(VMAllocation vmAllocation) {
		vmAllocation.setCpu(0);
	}

}
