package edu.uwo.csd.dcsim.examples.managers;

import java.util.ArrayList;
import java.util.List;

import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.management.HostStatus;
import edu.uwo.csd.dcsim.management.Policy;

public class HostMonitoringPolicy extends Policy<HostAutonomicManager> {

	ArrayList<Class<? extends Event>> triggerEvents = new ArrayList<Class<? extends Event>>();
	
	public HostMonitoringPolicy() {
		triggerEvents.add(HostMonitorEvent.class);
	}
	
	@Override
	public List<Class<? extends Event>> getTriggerEvents() {
		return triggerEvents;
	}

	@Override
	public boolean evaluateConditions(Event event, HostAutonomicManager context, Simulation simulation) {
		// no conditions
		return true;
	}

	@Override
	public void execute(Event event, HostAutonomicManager context, Simulation simulation) {
		
		HostStatus hostState = new HostStatus(context.getHost(), simulation);
		
		simulation.sendEvent(new HostStatusEvent(context.getParentManager(), hostState));
	}

}
