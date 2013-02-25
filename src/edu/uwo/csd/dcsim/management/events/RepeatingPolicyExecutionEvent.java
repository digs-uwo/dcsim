package edu.uwo.csd.dcsim.management.events;

import edu.uwo.csd.dcsim.core.RepeatingEvent;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.core.SimulationEventListener;

public class RepeatingPolicyExecutionEvent extends RepeatingEvent {

	public RepeatingPolicyExecutionEvent(Simulation simulation,
			SimulationEventListener target, long interval) {
		super(simulation, target, interval);
	}

}
