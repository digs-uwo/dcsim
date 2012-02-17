package edu.uwo.csd.dcsim2.host.resourcemanager;

import edu.uwo.csd.dcsim2.host.Cpu;
import edu.uwo.csd.dcsim2.vm.*;

public class StaticCpuManager extends CpuManager {

	//TODO should this be moved to a 'SimpleCpuManager' superclass, which implements basic functions
	//considering all CPUs a 'one big CPU' and leaves the allocation abstract?
	private int getAllocatedCpu() {
		int allocatedCPU = 0;
		
		for (VMAllocation vmAllocation : getHost().getVMAllocations()) {
			if (vmAllocation.getCpuAllocation() != null) {
				for (Integer coreCapacity : vmAllocation.getCpuAllocation().getCoreCapacityAlloc()) {
					allocatedCPU += coreCapacity;
				}
			}
		}
		
		return allocatedCPU;
	}
	
	private int getTotalCpu() {
		int totalCpu = 0;
		for (Cpu cpu : getHost().getCpus()) {
			totalCpu += cpu.getCores() * cpu.getCoreCapacity();
		}
		return totalCpu;
	}
	
	private int getAvailableCpu() {
		return getTotalCpu() - getAllocatedCpu();
	}
	
	@Override
	public boolean isCapable(VMDescription vmDescription) {
		//check cores and core capacity
		if (vmDescription.getVCores() * vmDescription.getVCoreCapacity() > getTotalCpu())
			return false;
		
		return true;
	}

	@Override
	public boolean hasCapacity(VMAllocationRequest vmAllocationRequest) {
		int requiredCapacity = 0;
		if (vmAllocationRequest.getCpuAllocation() != null) {
			for (Integer coreCapacity : vmAllocationRequest.getCpuAllocation().getCoreCapacityAlloc()) {
				requiredCapacity += coreCapacity;
			}
		}
		
		if (requiredCapacity <= getAvailableCpu())
			return true;
		
		return false;
	}

	@Override
	public boolean allocateResource(VMAllocationRequest vmAllocationRequest) {

		
		
		return false;
	}

	@Override
	public boolean deallocateResource(VMAllocationRequest vmAllocationRequest) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateAllocations() {
		// TODO Auto-generated method stub
		return false;
	}

}
