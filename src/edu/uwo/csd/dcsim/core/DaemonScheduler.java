package edu.uwo.csd.dcsim.core;

import java.util.ArrayList;

public abstract class DaemonScheduler implements SimulationEventListener {

	public static final int DAEMON_RUN_EVENT = 1;
	
	protected Simulation simulation;
	ArrayList<Daemon> daemons = new ArrayList<Daemon>();
	boolean running = false;
	
	public DaemonScheduler(Simulation simulation) {
		this.simulation = simulation;
	}
	
	public DaemonScheduler(Simulation simulation, Daemon daemon) {
		this(simulation);
		daemons.add(daemon);
	}

	@Override
	public final void handleEvent(Event e) {
		if (e.getType() == DAEMON_RUN_EVENT) {
			if (running) {
				for (Daemon daemon : daemons)
					daemon.run(simulation);
				simulation.sendEvent(new Event(DaemonScheduler.DAEMON_RUN_EVENT, getNextRunTime(), this, this));
			}
		}
	}
	
	public abstract long getNextRunTime();
	
	public final void start() {
		start(simulation.getSimulationTime());
	}
	
	public final void start(long time) {
		running = true;
		for (Daemon daemon : daemons)
			daemon.start(simulation);
		simulation.sendEvent(new Event(DaemonScheduler.DAEMON_RUN_EVENT, time, this, this));
	}
	
	public final void stop() {
		running = false;
		for (Daemon daemon : daemons)
			daemon.stop(simulation);
	}
	
}
