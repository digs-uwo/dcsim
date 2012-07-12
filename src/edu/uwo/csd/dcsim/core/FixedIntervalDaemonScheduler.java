package edu.uwo.csd.dcsim.core;

public class FixedIntervalDaemonScheduler extends DaemonScheduler {
	
	long frequency;
	
	public FixedIntervalDaemonScheduler(Simulation simulation, long frequency) {
		super(simulation);
		
		this.frequency = frequency;
	}
	
	public FixedIntervalDaemonScheduler(Simulation simulation, long frequency, Daemon daemon) {
		super(simulation, daemon);
		
		this.frequency = frequency;
	}

	@Override
	public long getNextRunTime() {
		return simulation.getSimulationTime() + frequency;
	}
	
	public void setFrequency(long frequency) {	
		this.frequency = frequency;
	}
	
	public long getFrequency() {
		return frequency;
	}

}
