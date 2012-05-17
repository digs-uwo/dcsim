package edu.uwo.csd.dcsim.vm;

import edu.uwo.csd.dcsim.application.*;
import edu.uwo.csd.dcsim.core.Simulation;

/**
 * Describes the general characteristics of a VM, and can instantiate new
 * instances of VMs based on the description
 * 
 * @author Michael Tighe
 *
 */
public class VMDescription {

	private int cores;
	private int coreCapacity;
	private int memory;	
	private int bandwidth;
	private long storage;
	private ApplicationFactory applicationFactory;
	
	public VMDescription(int cores, int coreCapacity, int memory, int bandwidth, long storage, ApplicationFactory applicationFactory) {
		this.cores = cores;
		this.coreCapacity = coreCapacity;
		this.memory = memory;
		this.bandwidth = bandwidth;
		this.storage = storage;
		this.applicationFactory = applicationFactory;
	}
	
	public VM createVM(Simulation simulation) {
		return new VM(simulation, this, applicationFactory.createApplication(simulation));
	}
	
	public int getCpu() {
		return cores * coreCapacity;
	}
	
	public int getCores() {
		return cores;
	}
	
	public int getCoreCapacity() {
		return coreCapacity;
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
