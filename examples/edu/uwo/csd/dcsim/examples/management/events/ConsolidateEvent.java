package edu.uwo.csd.dcsim.examples.management.events;

import edu.uwo.csd.dcsim.core.RepeatingEvent;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.core.SimulationEventListener;

public class ConsolidateEvent extends RepeatingEvent {

	public ConsolidateEvent(Simulation simulation,
			SimulationEventListener target, long interval) {
		super(simulation, target, interval);

	}

}
