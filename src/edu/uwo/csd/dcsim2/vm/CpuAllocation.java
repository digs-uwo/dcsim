package edu.uwo.csd.dcsim2.vm;

import java.util.Vector;

public class CpuAllocation {

	private Vector<Integer> coreCapacityAlloc;
	private VMAllocation vmAllocation;
	
	public CpuAllocation(VMAllocation vmAllocation) {
		this.vmAllocation = vmAllocation;
		coreCapacityAlloc = new Vector<Integer>();
	}
	
	public Vector<Integer> getCoreCapacityAlloc() {
		return coreCapacityAlloc;
	}
	
	public VMAllocation getVMAllocation() {
		return vmAllocation;
	}
	
}
