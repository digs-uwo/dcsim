package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.*;

import edu.uwo.csd.dcsim2.host.Cpu;
import edu.uwo.csd.dcsim2.vm.*;

public class StaticCpuManager extends CpuManager {
	
	@Override
	public boolean isCapable(VMDescription vmDescription) {
		//check cores and core capacity
		if (vmDescription.getCores() * vmDescription.getCoreCapacity() > getTotalCpu())
			return false;
		
		return true;
	}

	@Override
	public boolean hasCapacity(VMAllocationRequest vmAllocationRequest) {
		int requiredCapacity = 0;
		if (vmAllocationRequest.getCpuAllocation() != null) {
			for (Integer coreCapacity : vmAllocationRequest.getCpuAllocation().getCoreAlloc()) {
				requiredCapacity += coreCapacity;
			}
		}
		
		return requiredCapacity <= getAvailableCpu();
	}

	@Override
	public void allocateResource(VMAllocationRequest vmAllocationRequest, VMAllocation vmAllocation) {

		if (hasCapacity(vmAllocationRequest)) {
			CpuAllocation newAlloc = new CpuAllocation();
			for (Integer coreCapacity : vmAllocationRequest.getCpuAllocation().getCoreAlloc()) {
				newAlloc.getCoreAlloc().add(coreCapacity);
			}
			vmAllocation.setCpuAllocation(newAlloc);
			allocationMap.put(vmAllocation, newAlloc);			
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
