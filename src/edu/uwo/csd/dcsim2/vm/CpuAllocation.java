package edu.uwo.csd.dcsim2.vm;

import java.util.Vector;

public class CpuAllocation {

	private Vector<Integer> coreCapacityAlloc;
	
	public CpuAllocation() {
		coreCapacityAlloc = new Vector<Integer>();
	}
	
	public CpuAllocation(Vector<Integer> coreCapacityAlloc) {
		this.coreCapacityAlloc = coreCapacityAlloc;
	}
	
	public Vector<Integer> getCoreCapacityAlloc() {
		return coreCapacityAlloc;
	}
	
}
