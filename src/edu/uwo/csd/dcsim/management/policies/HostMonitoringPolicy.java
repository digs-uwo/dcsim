package edu.uwo.csd.dcsim.management.policies;

import edu.uwo.csd.dcsim.core.SimulationEventListener;
import edu.uwo.csd.dcsim.core.metrics.AvgValueMetric;
import edu.uwo.csd.dcsim.core.metrics.CountMetric;
import edu.uwo.csd.dcsim.management.*;
import edu.uwo.csd.dcsim.management.capabilities.HostManager;
import edu.uwo.csd.dcsim.management.events.HostStatusEvent;

public class HostMonitoringPolicy extends Policy {

	public static final String VM_STATES_SENT = "vmStatesSent";
	public static final String AVG_HOST_STATE_SIZE = "avgHostStateSize";
	
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
		
		if (simulation.isRecordingMetrics()) {
			CountMetric.getMetric(simulation, VM_STATES_SENT).add(hostState.getVms().size());
			AvgValueMetric.getMetric(simulation, AVG_HOST_STATE_SIZE).addValue(hostState.getVms().size());
		}
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
