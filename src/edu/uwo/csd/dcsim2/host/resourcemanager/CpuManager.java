package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.HashMap;
import java.util.Map;

import edu.uwo.csd.dcsim2.host.Cpu;
import edu.uwo.csd.dcsim2.vm.*;

public abstract class CpuManager extends ResourceManager {

	protected Map<VMAllocation, CpuAllocation> allocationMap = new HashMap<VMAllocation, CpuAllocation>();;
	
	public int getAllocatedCpu() {
		int allocatedCPU = 0;
		
		for (CpuAllocation cpuAllocation : allocationMap.values()) {
			for (Integer coreCapacity : cpuAllocation.getCoreAlloc()) {
				allocatedCPU += coreCapacity;
			}
		}
	
		return allocatedCPU;
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
	
	public double getCpuUtilization() {
		return getAllocatedCpu() / getTotalCpu();
	}
	
}
