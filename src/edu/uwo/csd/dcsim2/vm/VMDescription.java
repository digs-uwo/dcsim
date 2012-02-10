package edu.uwo.csd.dcsim2.vm;

import edu.uwo.csd.dcsim2.application.*;

public class VMDescription {

	private int vCores;
	private int vCoreCapacity;
	private int memory;	
	private int bandwidth;
	private long storage;
	private ApplicationFactory applicationFactory;
	
	public VMDescription(int vCores, int vCoreCapacity, int memory, int bandwidth, long storage, ApplicationFactory applicationFactory) {
		this.vCores = vCores;
		this.vCoreCapacity = vCoreCapacity;
		this.memory = memory;
		this.bandwidth = bandwidth;
		this.storage = storage;
		this.applicationFactory = applicationFactory;
	}
	
	public VM createVM() {
		return new VM(this, applicationFactory.createApplication());
	}
	
	public int getVCores() {
		return vCores;
	}
	
	public int getVCoreCapacity() {
		return vCoreCapacity;
	}
	
	public int getMemory() {
		return memory;
	}
	
	public int getBandwidth() {
		return bandwidth;
	}
	
	public long getStorage() {
		return storage;
	}
	
	public ApplicationFactory getApplicationFactory() {
		return applicationFactory;
	}
	
}
