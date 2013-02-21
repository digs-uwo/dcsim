package edu.uwo.csd.dcsim.examples.managers;

import java.util.ArrayList;
import java.util.List;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.management.*;

public class HostStatusPolicy extends Policy<DataCentreAutonomicManager> {

	ArrayList<Class<? extends Event>> triggerEvents = new ArrayList<Class<? extends Event>>();
	
	public HostStatusPolicy() {
		triggerEvents.add(HostStatusEvent.class);
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
		
		if (event instanceof HostStatusEvent) {
			HostStatusEvent hostStateEvent = (HostStatusEvent)event;
//			System.out.println("Received Host State from Host #" + hostState.getHostState().id);
			ArrayList<HostStatus> hostStates = context.getHostStatus(hostStateEvent.getHostState().getId());
			if (hostStates == null) {
				hostStates = new ArrayList<HostStatus>();
				context.getHostStatus().put(hostStateEvent.getHostState().getId(), hostStates);
			}
			
			hostStates.add(0, hostStateEvent.getHostState());
			if (hostStates.size() > 5) {
				hostStates.remove(hostStates.size() - 1);
			}
		}
		
	}

}
