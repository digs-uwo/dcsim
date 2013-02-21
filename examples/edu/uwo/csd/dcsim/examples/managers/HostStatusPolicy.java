package edu.uwo.csd.dcsim.examples.managers;

import java.util.ArrayList;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.management.*;

public class HostStatusPolicy extends Policy {

	ArrayList<Class<? extends Event>> triggerEvents = new ArrayList<Class<? extends Event>>();
	
	public HostStatusPolicy() {
		super(HostPoolManager.class);
	}

	public void execute(HostStatusEvent event) {		
		HostPoolManager hostPool = manager.getCapability(HostPoolManager.class);
		
		if (event instanceof HostStatusEvent) {
			HostStatusEvent hostStateEvent = (HostStatusEvent)event;

			ArrayList<HostStatus> hostStates = hostPool.getHostStatus(hostStateEvent.getHostState().getId());
			if (hostStates == null) {
				hostStates = new ArrayList<HostStatus>();
				hostPool.getHostStatus().put(hostStateEvent.getHostState().getId(), hostStates);
			}
			
			hostStates.add(0, hostStateEvent.getHostState());
			if (hostStates.size() > 5) {
				hostStates.remove(hostStates.size() - 1);
			}
		}
		
	}

}
