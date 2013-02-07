package edu.uwo.csd.dcsim.examples.managers;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.management.Policy;

public abstract class HostPolicy extends Policy<HostAutonomicManager> {

	@Override
	public abstract boolean evaluateConditions(Event event, HostAutonomicManager context);

	@Override
	public abstract void execute(Event event, HostAutonomicManager context);

}
