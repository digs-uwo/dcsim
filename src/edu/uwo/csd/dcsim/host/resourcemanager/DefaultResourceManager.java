package edu.uwo.csd.dcsim.host.resourcemanager;

import java.util.Collection;

import edu.uwo.csd.dcsim.vm.VMAllocation;
import edu.uwo.csd.dcsim.vm.VMAllocationRequest;

/**
 * The DefaultResourceManager implements a resource manager that oversubscribes CPU, and uses fixed, non-oversubscribed allocations for all other resources.
 * 
 * @author Michael Tighe
 *
 */
public class DefaultResourceManager extends ResourceManager {

	@Override
	public boolean hasCapacity(int cpu, int memory, int bandwidth, long storage) {
		
		//note that we don't check CPU, as we are oversubscribing it
		return 	(memory <= getAvailableMemory()) &&
				(bandwidth <= getAvailableBandwidth()) &&
				(storage <= getAvailableStorage());
		
	}

	@Override
	public boolean hasCapacity(VMAllocationRequest vmAllocationRequest) {
		return hasCapacity(vmAllocationRequest.getCpu(), vmAllocationRequest.getMemory(), vmAllocationRequest.getBandwidth(), vmAllocationRequest.getStorage());
	}

	@Override
	public boolean hasCapacity(Collection<VMAllocationRequest> vmAllocationRequests) {
		int totalCpu = 0;
		int totalMemory = 0;
		int totalBandwidth = 0;
		int totalStorage = 0;
		
		for (VMAllocationRequest allocationRequest : vmAllocationRequests) {
			totalCpu += allocationRequest.getCpu();
			totalMemory += allocationRequest.getMemory();
			totalBandwidth += allocationRequest.getBandwidth();
			totalStorage += allocationRequest.getStorage();
		}
		
		return hasCapacity(totalCpu, totalMemory, totalBandwidth, totalStorage);
	}

	@Override
	public boolean allocateResource(VMAllocationRequest vmAllocationRequest,
			VMAllocation vmAllocation) {
		
		if (hasCapacity(vmAllocationRequest)) {
			vmAllocation.setCpu(vmAllocationRequest.getCpu());
			vmAllocation.setMemory(vmAllocationRequest.getMemory());
			vmAllocation.setBandwidth(vmAllocationRequest.getBandwidth());
			vmAllocation.setStorage(vmAllocationRequest.getStorage());
			
			return true;
		}
		
		return false;
	}

	@Override
	public void deallocateResource(VMAllocation vmAllocation) {
		vmAllocation.setCpu(0);
		vmAllocation.setMemory(0);
		vmAllocation.setBandwidth(0);
		vmAllocation.setStorage(0);
	}

	@Override
	public void allocatePrivDomain(VMAllocation privDomainAllocation,
			int cpu, int memory, int bandwidth, long storage) {
		
		if (hasCapacity(cpu, memory, bandwidth, storage)) {
			privDomainAllocation.setCpu(cpu);
			privDomainAllocation.setMemory(memory);
			privDomainAllocation.setBandwidth(bandwidth);
			privDomainAllocation.setStorage(storage);
		} else {
			throw new RuntimeException("Could not allocate privileged domain on Host #" + getHost().getId());
		}
		
	}

}
