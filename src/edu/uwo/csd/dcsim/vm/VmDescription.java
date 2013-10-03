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
public class VmDescription {

	private int cores;
	private int coreCapacity;
	private int memory;	
	private int bandwidth;
	private int storage;
	private Task task;
	
	public VmDescription(Task task) {
		this.cores = task.getResourceSize().getCores();
		this.coreCapacity = task.getResourceSize().getCoreCapacity();
		this.memory = task.getResourceSize().getMemory();
		this.bandwidth = task.getResourceSize().getBandwidth();
		this.storage = task.getResourceSize().getStorage();
		this.task = task;
	}
	
	public VmDescription(int cores, int coreCapacity, int memory, int bandwidth, int storage, Task task) {
		this.cores = cores;
		this.coreCapacity = coreCapacity;
		this.memory = memory;
		this.bandwidth = bandwidth;
		this.storage = storage;
		this.task = task;
	}
	
	public Vm createVM(Simulation simulation) {
		return new Vm(simulation, this, task.createInstance());
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
	
	public int getStorage() {
		return storage;
	}
	
	public Task getTask() {
		return task;
	}
	
}
