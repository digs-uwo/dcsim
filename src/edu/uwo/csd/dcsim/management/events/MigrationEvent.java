package edu.uwo.csd.dcsim.management.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.management.AutonomicManager;

public class MigrationEvent extends Event {

	private AutonomicManager sourceHostManager;
	private Host targetHost;
	private int vmId;
	private boolean shutdownIfEmpty = false;
	
	public MigrationEvent(AutonomicManager sourceHostManager, Host targetHost, int vmId) {
		super(sourceHostManager);
		
		this.sourceHostManager = sourceHostManager;
		this.targetHost = targetHost;
		this.vmId = vmId;
	}
	
	public MigrationEvent(AutonomicManager sourceHostManager, Host targetHost, int vmId, boolean shutdownIfEmpty) {
		super(sourceHostManager);
		
		this.sourceHostManager = sourceHostManager;
		this.targetHost = targetHost;
		this.vmId = vmId;
		this.shutdownIfEmpty = shutdownIfEmpty;
	}
	
	public AutonomicManager getSourceHostManager() {
		return sourceHostManager;
	}
	
	public Host getTargetHost() {
		return targetHost;
	}
	
	public int getVmId() {
		return vmId;
	}
	
	public boolean shutdownIfEmpty() {
		return shutdownIfEmpty;
	}

}
