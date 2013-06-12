package edu.uwo.csd.dcsim.application;

import edu.uwo.csd.dcsim.core.Simulation;

/**
 * A tier of InteractiveApplications 
 * 
 * @author Michael Tighe
 *
 */
public class InteractiveApplicationTier extends ApplicationTier {

	private double cpuPerWork;
	private double cpuOverhead;
	private double bandwidth;
	private int memory;
	private long storage;
	
	
	public InteractiveApplicationTier(int memory, double bandwidth, long storage, double cpuPerWork, double cpuOverhead) {
		this.memory = memory;
		this.bandwidth = bandwidth;
		this.storage = storage;
		this.cpuPerWork = cpuPerWork;
		this.cpuOverhead = cpuOverhead;
	}
	
	
	
	@Override
	protected InteractiveApplication instantiateApplication(Simulation simulation) {
		return new InteractiveApplication(simulation, this, memory, bandwidth, storage, cpuPerWork, cpuOverhead);
	}


}
