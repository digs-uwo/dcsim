package edu.uwo.csd.dcsim2.application;

public class StaticApplicationFactory extends ApplicationFactory {

	private int cores;
	private int coreCapacity;
	private int memory;
	private int bandwidth;
	private long storage;
	
	public StaticApplicationFactory(int cores, int coreCapacity, int memory, int bandwidth, long storage) {
		
		this.memory = memory;
		this.bandwidth = bandwidth;
		this.storage = storage;
	}
	
	@Override
	public Application createApplication() {
		return new StaticApplication(cores, coreCapacity, memory, bandwidth, storage);
	}

}
