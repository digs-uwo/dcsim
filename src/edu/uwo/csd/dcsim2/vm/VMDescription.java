package edu.uwo.csd.dcsim2.vm;

public class VMDescription {

	private int vCores;
	private int vCoreCapacity;
	private int memory;	
	private int bandwidth;
	private long storage;
	
	public int getVCores() {
		return vCores;
	}
	
	public void setVCores(int vCores) {
		this.vCores = vCores;
	}
	
	public int getVCoreCapacity() {
		return vCoreCapacity;
	}
	
	public void setVCoreCapacity(int vCoreCapacity) {
		this.vCoreCapacity = vCoreCapacity;
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
	
	public void setStorage(int storage) {
		this.storage = storage;
	}
	
}
