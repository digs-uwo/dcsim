package edu.uwo.csd.dcsim.core;

import java.util.ArrayList;

public class DaemonScheduler implements SimulationEventListener {

	public static final int DAEMON_RUN_EVENT = 1;
	
	Simulation simulation;
	ArrayList<Daemon> daemons = new ArrayList<Daemon>();
	boolean running = false;
	long frequency;
	
	public DaemonScheduler(Simulation simulation, long frequency) {
		this.simulation = simulation;
		this.frequency = frequency;
	}
	
	public DaemonScheduler(Simulation simulation, long frequency, Daemon daemon) {
		this(simulation, frequency);
		daemons.add(daemon);
	}

	@Override
	public void handleEvent(Event e) {
		if (e.getType() == DAEMON_RUN_EVENT) {
			if (running) {
				for (Daemon daemon : daemons)
					daemon.run(simulation);
				simulation.sendEvent(new Event(DaemonScheduler.DAEMON_RUN_EVENT, simulation.getSimulationTime() + frequency, this, this));
			}
		}
	}
	
	public void setFrequency(long frequency) {	
		this.frequency = frequency;
	}
	
	public long getFrequency() {
		return frequency;
	}
	
	public void start() {
		start(simulation.getSimulationTime());
	}
	
	public void start(long time) {
		running = true;
		for (Daemon daemon : daemons)
			daemon.start(simulation);
		simulation.sendEvent(new Event(DaemonScheduler.DAEMON_RUN_EVENT, time, this, this));
	}
	
	public void stop() {
		running = false;
		for (Daemon daemon : daemons)
			daemon.stop(simulation);
	}
	
}
