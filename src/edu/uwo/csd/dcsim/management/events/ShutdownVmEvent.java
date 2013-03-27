package edu.uwo.csd.dcsim.management.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.management.AutonomicManager;

public class ShutdownVmEvent extends Event {

	private int hostId;
	private int vmId;
	private boolean log = true;
	
	public ShutdownVmEvent(AutonomicManager target, int hostId, int vmId) {
		super(target);
		this.hostId = hostId;
		this.vmId = vmId;
	}

	public int getHostId() {
		return hostId;
	}
	
	public int getVmId() {
		return vmId;
	}
	
	public void setLog(boolean log) {
		this.log = log;
	}
	
	@Override
	public void postExecute() {
		if (log) simulation.getTraceLogger().info("#vc," + vmId + "," + hostId);
	}
	
}
