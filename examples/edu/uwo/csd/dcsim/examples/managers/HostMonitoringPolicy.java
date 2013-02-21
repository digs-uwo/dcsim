package edu.uwo.csd.dcsim.examples.managers;

import edu.uwo.csd.dcsim.management.*;

public class HostMonitoringPolicy extends Policy {

	public HostMonitoringPolicy() {
		super(HostManager.class);
	}

	public void execute(HostMonitorEvent event) {
		
		HostManager hostManager = manager.getCapability(HostManager.class);
		HierarchicalManager hierarchicalManager = manager.getCapability(HierarchicalManager.class);
		
		HostStatus hostState = new HostStatus(hostManager.getHost(), simulation);
		
		simulation.sendEvent(new HostStatusEvent(hierarchicalManager.getParent(), hostState));
	}

}
