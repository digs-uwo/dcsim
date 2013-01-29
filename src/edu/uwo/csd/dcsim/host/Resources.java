package edu.uwo.csd.dcsim.host;

/**
 * Represents a set of resources
 * 
 * @author Michael Tighe
 *
 */
public class Resources {
	
	private double cpu = 0;
	private int memory = 0;
	private double bandwidth = 0;
	private long storage = 0;

	public Resources add(Resources v2) {
		
		Resources sum = new Resources();
		
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
	
	public Resources subtract(Resources v2) {
			
		Resources difference = new Resources();
		
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
	
	public double getCpu() {
		return cpu;
	}
	
	public void setCpu(double cpu) {
		this.cpu = cpu;
	}
	
	public int getMemory() {
		return memory;
	}
	
	public void setMemory(int memory) {
		this.memory = memory;
	}
	
	public double getBandwidth() {
		return bandwidth;
	}
	
	public void setBandwidth(double bandwidth) {
		this.bandwidth = bandwidth;
	}
	
	public long getStorage() {
		return storage;
	}
	
	public void setStorage(long storage) {
		this.storage = storage;
	}
	
}
