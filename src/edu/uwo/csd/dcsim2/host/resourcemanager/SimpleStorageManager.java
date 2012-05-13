package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.Collection;

import edu.uwo.csd.dcsim2.vm.*;

/**
 * SimpleStorageManager allocates storage to VMs on the Host provided that there is enough storage available to satisfy the entire requested amount
 * 
 * @author Michael Tighe
 *
 */
public class SimpleStorageManager extends StorageManager {

	@Override
	public boolean isCapable(VMDescription vmDescription) {
		return vmDescription.getStorage() <= getTotalStorage();
	}

	@Override
	public boolean hasCapacity(long storage) {
		return storage <= getAvailableStorage();
	}
	
	@Override
	public boolean hasCapacity(VMAllocationRequest vmAllocationRequest) {
		return hasCapacity(vmAllocationRequest.getStorage());
	}
	
	@Override
	public boolean hasCapacity(Collection<VMAllocationRequest> vmAllocationRequests) {

		long totalAlloc = 0;
		
		for (VMAllocationRequest allocationRequest : vmAllocationRequests)
			totalAlloc += allocationRequest.getStorage();

		return totalAlloc <= getAvailableStorage();
	}

	@Override
	public boolean allocateResource(VMAllocationRequest vmAllocationRequest,
			VMAllocation vmAllocation) {
		
		if (hasCapacity(vmAllocationRequest)) {
			long newAlloc = vmAllocationRequest.getStorage();
			vmAllocation.setStorage(newAlloc);
			
			return true;
		}
		
		return false;
	}

	@Override
	public void deallocateResource(VMAllocation vmAllocation) {
		vmAllocation.setStorage(0);
	}

	@Override
	public void allocatePrivDomain(VMAllocation privDomainAllocation, long allocation) {
		if (hasCapacity(allocation)) {
			privDomainAllocation.setStorage(allocation);
		} else {
			throw new RuntimeException("Could not allocate privileged domain on Host #" + getHost().getId());
		}
	}
	
}
