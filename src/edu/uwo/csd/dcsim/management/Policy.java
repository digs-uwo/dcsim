package edu.uwo.csd.dcsim.management;

import java.util.List;

import edu.uwo.csd.dcsim.core.*;

public abstract class Policy<T extends AutonomicManager> {

	private boolean enabled = true;
	
	//public abstract void enactPolicy(AutonomicManager context); //possibly required for initial setup TODO remove if not used
	public abstract List<Class <? extends Event>> getTriggerEvents();
	public abstract boolean evaluateConditions(Event event, T context, Simulation simulation);
	public abstract void execute(Event event, T context, Simulation simulation);
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
