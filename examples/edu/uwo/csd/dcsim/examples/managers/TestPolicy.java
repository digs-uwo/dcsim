package edu.uwo.csd.dcsim.examples.managers;

import java.util.ArrayList;
import java.util.List;

import edu.uwo.csd.dcsim.core.Event;

public class TestPolicy extends HostPolicy {

	ArrayList<Class<? extends Event>> triggerEvents = new ArrayList<Class<? extends Event>>();
	
	int count = 2;
	
	public TestPolicy() {
		triggerEvents.add(ConsolidateEvent.class);
	}
	
	@Override
	public List<Class<? extends Event>> getTriggerEvents() {
		return triggerEvents;
	}

	@Override
	public boolean evaluateConditions(Event event, HostAutonomicManager context) {
		// no conditions
		return true;
	}

	@Override
	public void execute(Event event, HostAutonomicManager context) {
		System.out.println("Executing TestPolicy");
		
		if (count > 0) {
			--count;
		} else {
			if (event instanceof ConsolidateEvent) {
				((ConsolidateEvent)event).stop();
			}
		}
	}

}
