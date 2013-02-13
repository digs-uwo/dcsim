package edu.uwo.csd.dcsim.examples.managers;

import java.util.ArrayList;
import java.util.List;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.management.Policy;

public class ConsolidationPolicy extends Policy<DataCentreAutonomicManager> {

	ArrayList<Class<? extends Event>> triggerEvents = new ArrayList<Class<? extends Event>>();
	
	public ConsolidationPolicy() {
		triggerEvents.add(ConsolidateEvent.class);
	}
	
	@Override
	public List<Class<? extends Event>> getTriggerEvents() {
		return triggerEvents;
	}

	@Override
	public boolean evaluateConditions(Event event, DataCentreAutonomicManager context,
			Simulation simulation) {

		return true;
	}

	@Override
	public void execute(Event event, DataCentreAutonomicManager context,
			Simulation simulation) {

		System.out.println("Consolidate!");

	}

}
