package edu.uwo.csd.dcsim.examples.managers;

import java.util.ArrayList;
import java.util.List;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.management.Policy;

public class HostStatePolicy extends Policy<DataCentreManager> {

	ArrayList<Class<? extends Event>> triggerEvents = new ArrayList<Class<? extends Event>>();
	
	public HostStatePolicy() {
		triggerEvents.add(HostStateEvent.class);
	}
	
	@Override
	public List<Class<? extends Event>> getTriggerEvents() {
		return triggerEvents;
	}

	@Override
	public boolean evaluateConditions(Event event, DataCentreManager context,
			Simulation simulation) {
		return true;
	}

	@Override
	public void execute(Event event, DataCentreManager context,
			Simulation simulation) {
		
		if (event instanceof HostStateEvent) {
			HostStateEvent hostState = (HostStateEvent)event;
			System.out.println("Received Host State from Host #" + hostState.getHostState().id);
		}
		
	}

}
