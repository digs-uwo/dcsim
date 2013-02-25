package edu.uwo.csd.dcsim.management.policies;

import edu.uwo.csd.dcsim.core.SimulationEventListener;
import edu.uwo.csd.dcsim.management.*;
import edu.uwo.csd.dcsim.management.capabilities.HostManager;
import edu.uwo.csd.dcsim.management.events.HostStatusEvent;

public class HostMonitoringPolicy extends Policy {

	SimulationEventListener target;
	
	public HostMonitoringPolicy(SimulationEventListener target) {
		addRequiredCapability(HostManager.class);
		
		this.target = target;
	}

	@Override
	public void onManagerStop() {
		//execute the monitor so that a final message is sent indicating that the host is now OFF
		execute();
	}
	
	public void execute() {		
		HostManager hostManager = manager.getCapability(HostManager.class);
		
		HostStatus hostState = new HostStatus(hostManager.getHost(), simulation.getSimulationTime());
		
		simulation.sendEvent(new HostStatusEvent(target, hostState));
	}

	@Override
	public void onInstall() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onManagerStart() {
		// TODO Auto-generated method stub
		
	}

}
