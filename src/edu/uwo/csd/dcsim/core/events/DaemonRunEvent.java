package edu.uwo.csd.dcsim.core.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.SimulationEventListener;

public class DaemonRunEvent extends Event {

	public DaemonRunEvent(SimulationEventListener daemon) {
		super(daemon);
	}

}
