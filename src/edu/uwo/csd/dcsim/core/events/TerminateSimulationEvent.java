package edu.uwo.csd.dcsim.core.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.Simulation;

public class TerminateSimulationEvent extends Event {

	public TerminateSimulationEvent(Simulation simulation) {
		super(simulation);

	}
	
	@Override
	public void log() {
		simulation.getLogger().info("Simulation terminating");
	}

}
