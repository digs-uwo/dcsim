package edu.uwo.csd.dcsim.management.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.management.AutonomicManager;

public class MigrationCompletedEvent extends Event {

	private int sourceHostId;
	private int targetHostId;
	private int vmId;
	
	public MigrationCompletedEvent(AutonomicManager target, int sourceHostId, int targetHostId, int vmId) {
		super(target);
		
		this.sourceHostId = sourceHostId;
		this.targetHostId = targetHostId;
		this.vmId = vmId;
	}
	
	public int getSourceHostId() {
		return sourceHostId;
	}
	
	public int getTargetHostId() {
		return targetHostId;
	}
	
	public int getVmId() {
		return vmId;
	}
}
