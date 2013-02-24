package edu.uwo.csd.dcsim.management.capabilities;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.SimulationEventListener;
import edu.uwo.csd.dcsim.management.AutonomicManager;

public abstract class HostCapability implements SimulationEventListener {
	
	private AutonomicManager manager;
	
	public final void setAutonomicManager(AutonomicManager manager) {
		this.manager = manager;
	}
	
	public final AutonomicManager getManager() {
		return manager;
	}

	@Override
	public final void handleEvent(Event e) {
		//forward event to manager
		manager.handleEvent(e);
	}
	
	
	
}
