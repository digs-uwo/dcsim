package edu.uwo.csd.dcsim.core;

public class FixedIntervalDaemonScheduler implements DaemonScheduler, SimulationEventListener {
	
	public static final int DAEMON_RUN_EVENT = 1;
	
	long frequency;
	protected Simulation simulation;
	Daemon daemon;
	boolean running = false;
	boolean enabled = true;
	
	public FixedIntervalDaemonScheduler(Simulation simulation, long frequency, Daemon daemon) {
		this.simulation = simulation;
		this.daemon = daemon;
		this.frequency = frequency;
	}
	
	public final void start() {
		start(simulation.getSimulationTime());
	}
	
	public final void start(long time) {
		running = true;
		daemon.onStart(simulation);
		simulation.sendEvent(new Event(DAEMON_RUN_EVENT, time, this, this));
	}
	
	public final void stop() {
		running = false;
		daemon.onStop(simulation);
	}
	
	public long getNextRunTime() {
		return simulation.getSimulationTime() + frequency;
	}
	
	@Override
	public final void handleEvent(Event e) {
		if (e.getType() == DAEMON_RUN_EVENT) {
			if (running) {
				if (enabled)
					daemon.run(simulation);
				simulation.sendEvent(new Event(DAEMON_RUN_EVENT, getNextRunTime(), this, this));
			}
		}
	}

	public void setFrequency(long frequency) {	
		this.frequency = frequency;
	}
	
	public long getFrequency() {
		return frequency;
	}
	
	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
