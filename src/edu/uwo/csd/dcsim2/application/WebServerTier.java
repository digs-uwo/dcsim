package edu.uwo.csd.dcsim2.application;

import edu.uwo.csd.dcsim2.core.Simulation;

public class WebServerTier extends ApplicationTier {

	private double cpuPerWork;
	private double bwPerWork;
	private double cpuOverhead;
	private int memory;
	private long storage;
	
	public WebServerTier(int memory, long storage, double cpuPerWork, double bwPerWork, double cpuOverhead) {
		this.memory = memory;
		this.storage = storage;
		this.cpuPerWork = cpuPerWork;
		this.bwPerWork = bwPerWork;
		this.cpuOverhead = cpuOverhead;
	}
	
	@Override
	protected WebServerApplication instantiateApplication(Simulation simulation) {
		return new WebServerApplication(simulation, this, memory, storage, cpuPerWork, bwPerWork, cpuOverhead);
	}

}
