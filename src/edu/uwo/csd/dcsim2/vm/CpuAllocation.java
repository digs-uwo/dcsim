package edu.uwo.csd.dcsim2.vm;

import java.util.ArrayList;

public class CpuAllocation {

	private ArrayList<Integer> coreAlloc;
	
	public CpuAllocation() {
		coreAlloc = new ArrayList<Integer>();
	}
	
	public CpuAllocation(ArrayList<Integer> coreAlloc) {
		this.coreAlloc = coreAlloc;
	}
	
	public ArrayList<Integer> getCoreAlloc() {
		return coreAlloc;
	}
	
	public int getTotalAlloc() {
		int total = 0;
		for (Integer i : coreAlloc) {
			total += i;
		}
		return total;
	}
	
}
