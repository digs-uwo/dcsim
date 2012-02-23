package edu.uwo.csd.dcsim2.vm;

import java.util.ArrayList;

public class VirtualResources {
	
	private int cpu = 0;
	private int memory = 0;
	private int bandwidth = 0;
	private long storage = 0;
	
	public VirtualResources add(VirtualResources v2) {
		
		VirtualResources sum = new VirtualResources();
		
		/* 
		 * CPU and Bandwidth values are summed
		 */
		sum.setCpu(this.getCpu() + v2.getCpu());
		sum.setBandwidth(this.getBandwidth() + v2.getBandwidth());
		
		/*
		 * For memory and storage, take the max value
		 */
		sum.setMemory(Math.max(this.getMemory(), v2.getMemory()));
		sum.setStorage(Math.max(this.getStorage(), v2.getStorage()));
		
		return sum;
	}
	
	public VirtualResources subtract(VirtualResources v2) {
			
		VirtualResources difference = new VirtualResources();
		
		/* 
		 * CPU and Bandwidth values are summed
		 */
		difference.setCpu(this.getCpu() - v2.getCpu());
		difference.setBandwidth(this.getBandwidth() - v2.getBandwidth());
		
		/*
		 * For memory and storage, take the max value
		 * TODO is this correct? Can this even be defined?
		 */
		difference.setMemory(Math.max(this.getMemory(), v2.getMemory()));
		difference.setStorage(Math.max(this.getStorage(), v2.getStorage()));
		
		return difference;
	}
	
	public VirtualResources() {
		
	}
	
	public int getCpu() {
		return cpu;
	}
	
	public void setCpu(int cpu) {
		this.cpu = cpu;
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
