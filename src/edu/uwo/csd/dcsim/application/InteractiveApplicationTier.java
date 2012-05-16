package edu.uwo.csd.dcsim.application;

import edu.uwo.csd.dcsim.core.Simulation;

public class InteractiveApplicationTier extends ApplicationTier {

	private double cpuPerWork;
	private double bwPerWork;
	private double cpuOverhead;
	private int memory;
	private long storage;
	
	public InteractiveApplicationTier(int memory, long storage, double cpuPerWork, double bwPerWork, double cpuOverhead) {
		this.memory = memory;
		this.storage = storage;
		this.cpuPerWork = cpuPerWork;
		this.bwPerWork = bwPerWork;
		this.cpuOverhead = cpuOverhead;
	}
	
	@Override
	protected InteractiveApplication instantiateApplication(Simulation simulation) {
		return new InteractiveApplication(simulation, this, memory, storage, cpuPerWork, bwPerWork, cpuOverhead);
	}

}
