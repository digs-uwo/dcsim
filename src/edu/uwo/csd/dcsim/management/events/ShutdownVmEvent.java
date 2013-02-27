package edu.uwo.csd.dcsim.management.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.management.AutonomicManager;

public class ShutdownVmEvent extends Event {

	private int hostId;
	private int vmId;
	
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
	
	@Override
	public void postExecute() {
		simulation.getTraceLogger().info("#vc," + vmId + "," + hostId);
	}
	
}
