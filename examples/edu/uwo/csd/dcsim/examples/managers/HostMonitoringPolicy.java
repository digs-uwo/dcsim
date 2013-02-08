package edu.uwo.csd.dcsim.examples.managers;

import java.util.ArrayList;
import java.util.List;

import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.management.Policy;

public class HostMonitoringPolicy extends Policy<HostAutonomicManager> {

	ArrayList<Class<? extends Event>> triggerEvents = new ArrayList<Class<? extends Event>>();
	
	public HostMonitoringPolicy() {
		triggerEvents.add(HostMonitorEvent.class);
	}
	
	@Override
	public List<Class<? extends Event>> getTriggerEvents() {
		return triggerEvents;
	}

	@Override
	public boolean evaluateConditions(Event event, HostAutonomicManager context, Simulation simulation) {
		// no conditions
		return true;
	}

	@Override
	public void execute(Event event, HostAutonomicManager context, Simulation simulation) {
		
		HostStateEvent.HostState hostState = new HostStateEvent.HostState();
		hostState.id = context.getHost().getId();
		hostState.cpuInUse = (int)Math.ceil(context.getHost().getResourceManager().getCpuInUse());
		hostState.cpuTotal = context.getHost().getTotalCpu();
		hostState.memoryInUse = context.getHost().getResourceManager().getAllocatedMemory();
		hostState.memoryTotal = context.getHost().getMemory();
		hostState.bandwidthInUse = context.getHost().getResourceManager().getAllocatedBandwidth();
		hostState.bandwidthTotal = context.getHost().getBandwidth();
		hostState.storageInUse = context.getHost().getResourceManager().getAllocatedStorage();
		hostState.storageTotal = context.getHost().getStorage();
		hostState.powerConsumption = (int)Math.ceil(context.getHost().getCurrentPowerConsumption());
		
		simulation.sendEvent(new HostStateEvent(context.getParentManager(), hostState));
	}

}
