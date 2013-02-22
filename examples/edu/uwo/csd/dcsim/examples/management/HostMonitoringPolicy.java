package edu.uwo.csd.dcsim.examples.management;

import edu.uwo.csd.dcsim.core.SimulationEventListener;
import edu.uwo.csd.dcsim.examples.management.capabilities.HostManager;
import edu.uwo.csd.dcsim.examples.management.events.HostMonitorEvent;
import edu.uwo.csd.dcsim.examples.management.events.HostStatusEvent;
import edu.uwo.csd.dcsim.management.*;

public class HostMonitoringPolicy extends Policy {

	SimulationEventListener target;
	
	public HostMonitoringPolicy(SimulationEventListener target) {
		super(HostManager.class);
		
		this.target = target;
	}

	public void execute(HostMonitorEvent event) {		
		HostManager hostManager = manager.getCapability(HostManager.class);
		
		HostStatus hostState = new HostStatus(hostManager.getHost(), simulation);
		
		simulation.sendEvent(new HostStatusEvent(target, hostState));
	}

}
