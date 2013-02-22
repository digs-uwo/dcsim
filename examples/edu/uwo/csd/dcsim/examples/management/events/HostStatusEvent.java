package edu.uwo.csd.dcsim.examples.management.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.SimulationEventListener;
import edu.uwo.csd.dcsim.management.HostStatus;

public class HostStatusEvent extends Event {

	private HostStatus hostState;

	public HostStatusEvent(SimulationEventListener target, HostStatus hostState) {
		super(target);

		this.hostState = hostState;
	}

	public HostStatus getHostState() {
		return hostState;
	}
	
}
