package edu.uwo.csd.dcsim.examples.managers;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.SimulationEventListener;

public class HostStateEvent extends Event {

	private HostState hostState;

	public HostStateEvent(SimulationEventListener target, HostState hostState) {
		super(target);

		this.hostState = hostState;
	}

	public HostState getHostState() {
		return hostState;
	}
	
	public static class HostState {
		int id;
		int cpuInUse; //send as int instead of double for simplicity
		int cpuTotal;
		int memoryInUse;
		int memoryTotal;
		int bandwidthInUse;
		int bandwidthTotal;
		long storageInUse;
		long storageTotal;
		int powerConsumption; //send as int instead of double for simplicity
	}
	
}
