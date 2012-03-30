package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.HashMap;
import java.util.Map;

import edu.uwo.csd.dcsim2.host.Cpu;
import edu.uwo.csd.dcsim2.vm.*;

public abstract class CpuManager extends ResourceManager {

	protected Map<VMAllocation, CpuAllocation> allocationMap = new HashMap<VMAllocation, CpuAllocation>();;
	private VMAllocation privDomainAllocation;
	
	/*
	 * Physical CPU related methods
	 */
	
	/**
	 * Get the total physical CPU capacity of the host (total capacity of all CPUs and cores)
	 * @return
	 */
	public int getTotalPhysicalCpu() {
		int totalCpu = 0;
		for (Cpu cpu : getHost().getCpus()) {
			totalCpu += cpu.getCores() * cpu.getCoreCapacity();
		}
		return totalCpu;
	}
	
	/**
	 * Get the amount of physical CPU capacity in use (actively being used, not allocated)
	 * @return
	 */
	public double getPhysicalCpuInUse() {
		double cpuInUse = 0;
		
		if (privDomainAllocation != null) {
			cpuInUse += privDomainAllocation.getResourcesInUse().getCpu();
		}
		
		for (VMAllocation allocation : allocationMap.keySet()) {
			cpuInUse += allocation.getResourcesInUse().getCpu();
		}
		
		return cpuInUse;
	}
	
	/**
	 * Get the fraction of physical CPU capacity that is current in use (actively being used, not allocated)
	 * @return
	 */
	public double getCpuUtilization() {
		return getPhysicalCpuInUse() / getTotalPhysicalCpu();
	}
	
	/*
	 * CPU Allocation related methods
	 */
	
	/**
	 * Get the total size of the allocation space of the CPU. This may be larger than the physical capacity
	 * of the CPU to allow for oversubscription.
	 * @return
	 */
	public abstract int getTotalAllocationSize();
	
	/**
	 * Get the total amount of CPU that has been allocated. This value may be larger than the physical CPU
	 * capacity due to oversubscription, but will always be <= the total allocation size
	 * @return
	 */
	public int getAllocatedCpu() {
		int allocatedCpu = 0;
		
		if (privDomainAllocation != null) {
			allocatedCpu += privDomainAllocation.getCpuAllocation().getTotalAlloc();
		}
		
		for (CpuAllocation cpuAllocation : allocationMap.values()) {
			for (Integer coreCapacity : cpuAllocation.getCoreAlloc()) {
				allocatedCpu += coreCapacity;
			}
		}
	
		return allocatedCpu;
	}
	
	/**
	 * Get the amount of allocation space not yet allocated
	 * @return
	 */
	public int getAvailableAllocation() {
		return getTotalAllocationSize() - getAllocatedCpu();
	}
	
	/**
	 * Get the fraction of the allocation space that has been allocated
	 * @return
	 */
	public double getCpuAllocation() {
		return getAllocatedCpu() / getTotalAllocationSize();
	}
	
	public VMAllocation getPrivDomainAllocation() {
		return privDomainAllocation;
	}
	
	protected void setPrivDomainAllocation(VMAllocation privDomainAllocation) {
		this.privDomainAllocation = privDomainAllocation;
	}
	
}
