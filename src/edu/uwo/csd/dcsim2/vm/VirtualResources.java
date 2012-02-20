package edu.uwo.csd.dcsim2.vm;

import java.util.ArrayList;

public class VirtualResources {
	
	private ArrayList<Integer> cores = new ArrayList<Integer>();
	private int memory = 0;
	private int bandwidth = 0;
	private long storage = 0;
	
	public static VirtualResources add(VirtualResources v1, VirtualResources v2) {
		
		if (v2.getCores().size() != v1.getCores().size()) {
			//TODO throw exception
		}
		
		VirtualResources sum = new VirtualResources(v1.getCores().size());
		
		/* 
		 * CPU and Bandwidth values are summed
		 */
		for (int i = 0; i < v1.getCores().size(); ++i) {
			sum.getCores().add(v1.getCores().get(i) + v2.getCores().get(i));
		}
		
		sum.setBandwidth(v1.getBandwidth() + v2.getBandwidth());
		
		/*
		 * For memory and storage, take the max value
		 */
		sum.setMemory(Math.max(v1.getMemory(), v2.getMemory()));
		sum.setStorage(Math.max(v1.getStorage(), v2.getStorage()));
		
		return sum;
	}
	public static VirtualResources subtract(VirtualResources v1, VirtualResources v2) {
		
		if (v2.getCores().size() != v1.getCores().size()) {
			//TODO throw exception
		}
		
		VirtualResources difference = new VirtualResources(v1.getCores().size());
		
		/* 
		 * CPU and Bandwidth values are summed
		 */
		for (int i = 0; i < v1.getCores().size(); ++i) {
			difference.getCores().add(v1.getCores().get(i) - v2.getCores().get(i));
		}
		
		difference.setBandwidth(v1.getBandwidth() - v2.getBandwidth());
		
		/*
		 * For memory and storage, take the max value
		 * TODO is this correct? Can this even be defined?
		 */
		difference.setMemory(Math.max(v1.getMemory(), v2.getMemory()));
		difference.setStorage(Math.max(v1.getStorage(), v2.getStorage()));
		
		return difference;
	}
	
	public VirtualResources() {
		
	}
	
	public VirtualResources(int nCores) {
		for (int i = 0; i < nCores; ++i) {
			cores.add(0);
		}
	}
	
	public ArrayList<Integer> getCores() {
		return cores;
	}
	
	public int getMemory() {
		return memory;
	}
	
	public void setMemory(int memory) {
		this.memory = memory;
	}
	
	public int getBandwidth() {
		return bandwidth;
	}
	
	public void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}
	
	public long getStorage() {
		return storage;
	}
	
	public void setStorage(long storage) {
		this.storage = storage;
	}
	
}
