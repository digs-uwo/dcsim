package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.HashMap;
import java.util.Map;

import edu.uwo.csd.dcsim2.host.Cpu;
import edu.uwo.csd.dcsim2.vm.*;

public abstract class CpuManager extends ResourceManager {

	protected Map<VMAllocation, CpuAllocation> allocationMap = new HashMap<VMAllocation, CpuAllocation>();;
	private VMAllocation privDomainAllocation;
	
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
	
	public int getTotalCpu() {
		int totalCpu = 0;
		for (Cpu cpu : getHost().getCpus()) {
			totalCpu += cpu.getCores() * cpu.getCoreCapacity();
		}
		return totalCpu;
	}
	
	public int getAvailableCpu() {
		return getTotalCpu() - getAllocatedCpu();
	}
	
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
	
	public double getCpuUtilization() {
		return getCpuInUse() / getTotalCpu();
	}
	
	public double getCpuAllocation() {
		return getAllocatedCpu() / getTotalCpu();
	}
	
	public VMAllocation getPrivDomainAllocation() {
		return privDomainAllocation;
	}
	
	protected void setPrivDomainAllocation(VMAllocation privDomainAllocation) {
		this.privDomainAllocation = privDomainAllocation;
	}
	
}
