package edu.uwo.csd.dcsim.management.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.management.AutonomicManager;

public class VmInstantiationCompleteEvent extends Event {

	private int applicationId;
	private int taskId;
	private long instanceId;
	private int vmId;
	private int hostId;
	
	public VmInstantiationCompleteEvent(AutonomicManager target, int applicationId, int taskId, long instanceId, int vmId, int hostId) {
		super(target);
		
		this.applicationId = applicationId;
		this.taskId = taskId;
		this.instanceId = instanceId;
		this.vmId = vmId;
		this.hostId = hostId;
	}
	
	public int getApplicationId() {
		return applicationId;
	}
	
	public int getTaskId() {
		return taskId;
	}
	
	public long getInstanceId() {
		return instanceId;
	}
	
	public int getVmId() {
		return vmId;
	}
	
	public int getHostId() {
		return hostId;
	}

}
