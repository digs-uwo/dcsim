package edu.uwo.csd.dcsim.management;

import java.util.List;

import edu.uwo.csd.dcsim.core.*;

public abstract class Policy {

	private boolean enabled = true;
	
	//public abstract List<Event type? + Message type?> getTriggerEvents();
	
	public abstract boolean evaluateConditions(Event event, AutonomicManager context);
	public abstract void execute(Event event, AutonomicManager context);
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
