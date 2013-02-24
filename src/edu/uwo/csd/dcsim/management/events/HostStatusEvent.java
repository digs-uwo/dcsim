package edu.uwo.csd.dcsim.management.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.SimulationEventListener;
import edu.uwo.csd.dcsim.management.HostStatus;

public class HostStatusEvent extends Event {

	private HostStatus hostStatus;

	public HostStatusEvent(SimulationEventListener target, HostStatus hostStatus) {
		super(target);

		this.hostStatus = hostStatus;
	}

	public HostStatus getHostStatus() {
		return hostStatus;
	}
	
}
