package edu.uwo.csd.dcsim.management;

import edu.uwo.csd.dcsim.core.*;

/**
 * A policy to manage some aspect of the DataCentre.
 * 
 * @author Michael Tighe
 *
 */
public abstract class ManagementPolicy implements SimulationEventListener {

	public static final int MANAGEMENT_POLICY_EXECUTE_EVENT = 1;
	
	protected Simulation simulation;
	protected boolean running;
	
	public ManagementPolicy(Simulation simulation) {
		this.simulation = simulation;
		
		running = false;
	}
	
	public abstract void execute();
	public abstract long getNextExecutionTime();
	public abstract void processEvent(Event e);
	
	public final void start() {
		start(simulation.getSimulationTime());
	}
	
	public final void start(long time) {
		simulation.sendEvent(new Event(ManagementPolicy.MANAGEMENT_POLICY_EXECUTE_EVENT, time, this, this));
		running = true;
	}
	
	public final void stop() {
		running = false;
	}
	
	@Override
	public void handleEvent(Event e) {
		if (e.getType() == MANAGEMENT_POLICY_EXECUTE_EVENT) {
			if (running) {
				execute();
				simulation.sendEvent(new Event(ManagementPolicy.MANAGEMENT_POLICY_EXECUTE_EVENT, getNextExecutionTime(), this, this));
			}
		} else {
			processEvent(e);
		}
	}

	
}
