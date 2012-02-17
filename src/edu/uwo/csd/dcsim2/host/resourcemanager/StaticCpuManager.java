package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.*;

import edu.uwo.csd.dcsim2.host.Cpu;
import edu.uwo.csd.dcsim2.vm.*;

public class StaticCpuManager extends CpuManager {

	Map<VMAllocation, CpuAllocation> allocationMap;
	
	public StaticCpuManager() {
		allocationMap = new HashMap<VMAllocation, CpuAllocation>();
	}
	
	//TODO should this be moved to a 'SimpleCpuManager' superclass, which implements basic functions
	//considering all CPUs a 'one big CPU' and leaves the allocation abstract?
	private int getAllocatedCpu() {
		int allocatedCPU = 0;
		
		for (CpuAllocation cpuAllocation : allocationMap.values()) {
			for (Integer coreCapacity : cpuAllocation.getCoreCapacityAlloc()) {
				allocatedCPU += coreCapacity;
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
		
		return requiredCapacity <= getAvailableCpu();
	}

	@Override
	public void allocateResource(VMAllocationRequest vmAllocationRequest, VMAllocation vmAllocation) {

		if (hasCapacity(vmAllocationRequest)) {
			CpuAllocation newAlloc = new CpuAllocation();
			for (Integer coreCapacity : vmAllocationRequest.getCpuAllocation().getCoreCapacityAlloc()) {
				newAlloc.getCoreCapacityAlloc().add(coreCapacity);
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
