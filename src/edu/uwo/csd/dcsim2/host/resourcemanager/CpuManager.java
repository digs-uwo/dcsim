package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.HashMap;
import java.util.Map;

import edu.uwo.csd.dcsim2.core.Utility;
import edu.uwo.csd.dcsim2.vm.*;

public abstract class CpuManager extends ResourceManager {

	protected Map<VMAllocation, Integer> allocationMap = new HashMap<VMAllocation, Integer>();
	private VMAllocation privDomainAllocation;
	
	/*
	 * Physical CPU related methods
	 */
	
	/**
	 * Get the total physical CPU capacity of the host (total capacity of all CPUs and cores)
	 * @return
	 */
	public int getTotalCpu() {
		return getHost().getTotalCpu();
	}
	
	/**
	 * Get the amount of physical CPU capacity in use (real usage, not allocation)
	 * @return
	 */
	public double getCpuInUse() {
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
	 * Get the fraction of physical CPU capacity that is current in use (real usage, not allocation)
	 * @return
	 */
	public double getCpuUtilization() {
		return Utility.roundDouble(getCpuInUse() / getTotalCpu());
	}
	
	/**
	 * Get the amount of CPU not be used (real usage, not allocation)
	 * @return
	 */
	public double getUnusedCpu() {
		return getTotalCpu() - getCpuUtilization();
	}
	
	/*
	 * CPU Allocation methods
	 */
	
	/**
	 * Get the total amount of CPU that has been allocated. This value may be larger than the physical CPU
	 * capacity due to oversubscription, but will always be <= the total allocation size
	 * @return
	 */
	public int getAllocatedCpu() {
		int allocatedCpu = 0;
		
		if (privDomainAllocation != null) {
			allocatedCpu += privDomainAllocation.getCpu();
		}
		
		for (Integer cpuAllocation : allocationMap.values()) {
			allocatedCpu += cpuAllocation;
		}
	
		return allocatedCpu;
	}
	
	/**
	 * Get the amount of allocation space not yet allocated
	 * @return
	 */
	public int getAvailableAllocation() {
		return getTotalCpu() - getAllocatedCpu();
	}
		
	public VMAllocation getPrivDomainAllocation() {
		return privDomainAllocation;
	}
	
	protected void setPrivDomainAllocation(VMAllocation privDomainAllocation) {
		this.privDomainAllocation = privDomainAllocation;
	}
	
}
