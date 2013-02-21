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

	public Resources() {
		//allow default constructor
	}
	
	public Resources(Resources resources) {
		this(resources.getCpu(), resources.getMemory(), resources.getBandwidth(), resources.getStorage());
	}
	
	public Resources(double cpu, int memory, double bandwidth, long storage) {
		this.cpu = cpu;
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
