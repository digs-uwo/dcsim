package edu.uwo.csd.dcsim.management.policies;

import edu.uwo.csd.dcsim.core.SimulationEventListener;
import edu.uwo.csd.dcsim.management.*;
import edu.uwo.csd.dcsim.management.capabilities.HostManager;
import edu.uwo.csd.dcsim.management.events.HostMonitorEvent;
import edu.uwo.csd.dcsim.management.events.HostStatusEvent;

public class HostMonitoringPolicy extends Policy {

	SimulationEventListener target;
	
	public HostMonitoringPolicy(SimulationEventListener target) {
		addRequiredCapability(HostManager.class);
		
		this.target = target;
	}

	public void execute(HostMonitorEvent event) {		
		HostManager hostManager = manager.getCapability(HostManager.class);
		
		HostStatus hostState = new HostStatus(hostManager.getHost(), simulation);
		
		simulation.sendEvent(new HostStatusEvent(target, hostState));
	}

}
