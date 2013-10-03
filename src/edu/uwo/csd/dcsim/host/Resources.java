package edu.uwo.csd.dcsim.host;

/**
 * Represents a set of resources
 * 
 * @author Michael Tighe
 *
 */
public class Resources {
	
	private int cores = 1;
	private int cpu = 0;
	private int memory = 0;
	private int bandwidth = 0;
	private int storage = 0;

	public Resources() {
		//allow default constructor
	}
	
	public Resources(Resources resources) {
		this(resources.getCores(), resources.getCoreCapacity(), resources.getMemory(), resources.getBandwidth(), resources.getStorage());
	}
	
	public Resources(int cpu, int memory, int bandwidth, int storage) {
		this.cpu = cpu;
		this.memory = memory;
		this.bandwidth = bandwidth;
		this.storage = storage;
	}
	
	public Resources(int cores, int coreCapacity, int memory, int bandwidth, int storage) {
		this.cores = cores;
		this.cpu = cores * coreCapacity;
		this.memory = memory;
		this.bandwidth = bandwidth;
		this.storage = storage;
	}
	
	public Resources add(Resources v2) {
		
		Resources sum = new Resources();
		
		/* 
		 * Individual resource values are summed
		 */
		sum.setCpu(this.getCpu() + v2.getCpu());
		sum.setBandwidth(this.getBandwidth() + v2.getBandwidth());
		sum.setMemory(this.getMemory() + v2.getMemory());
		sum.setStorage(this.getStorage() + v2.getStorage());
		
		return sum;
	}
	
	public Resources subtract(Resources v2) {
			
		Resources difference = new Resources();
		
		/* 
		 * Individual resource values are substracted
		 */
		difference.setCpu(this.getCpu() - v2.getCpu());
		difference.setBandwidth(this.getBandwidth() - v2.getBandwidth());
		difference.setMemory(this.getMemory() - v2.getMemory());
		difference.setStorage(this.getStorage() - v2.getStorage());
		
		return difference;
	}
	
	public Resources copy() {
		return new Resources(this);
	}
	
	public int getCores() {
		return cores;
	}
	
	public void setCores(int cores) {
		this.cores = cores;
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
	
	public int getCoreCapacity() {
		return (int)cpu / cores;
	}
	
	public int getBandwidth() {
		return bandwidth;
	}
	
	public void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}
	
	public int getStorage() {
		return storage;
	}
	
	public void setStorage(int storage) {
		this.storage = storage;
	}
	
}
