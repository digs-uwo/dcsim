package edu.uwo.csd.dcsim.management;

import java.util.List;

import edu.uwo.csd.dcsim.core.*;

public abstract class Policy {

	private boolean enabled = true;
	
	//public abstract void enactPolicy(AutonomicManager context); //possibly required for initial setup TODO remove if not used
	public abstract List<Event> getTriggerEvents();
	public abstract boolean evaluateConditions(Event event, AutonomicManager context);
	public abstract void execute(Event event, AutonomicManager context);
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
