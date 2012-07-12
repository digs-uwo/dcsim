package edu.uwo.csd.dcsim.core;

public abstract class Monitor {

	protected Simulation simulation;
	protected long frequency;
	long lastExecution = 0;
	protected boolean enabled = true;
		
	public Monitor(Simulation simulation, long frequency) {
		this.simulation = simulation;
		this.frequency = frequency;
	}
	
	public final long run() {
		if (lastExecution + frequency <= simulation.getSimulationTime()) {
			if (enabled)
				execute();
			lastExecution = simulation.getSimulationTime();
		}
		
		return lastExecution + frequency;
	}
	
	public abstract void execute();
	
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public final boolean getEnabled() {
		return enabled;
	}

	public final void setFrequency(long frequency) {
		this.frequency = frequency;
	}
	
	public final long getFrequency() {
		return frequency;
	}
	
}
