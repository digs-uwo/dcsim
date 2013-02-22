package edu.uwo.csd.dcsim.examples.managers;

import edu.uwo.csd.dcsim.core.SimulationEventListener;
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
