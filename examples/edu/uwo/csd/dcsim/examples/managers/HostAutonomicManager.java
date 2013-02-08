package edu.uwo.csd.dcsim.examples.managers;

import edu.uwo.csd.dcsim.management.*;
import edu.uwo.csd.dcsim.host.*;

public class HostAutonomicManager extends AutonomicManager {

	private Host host;
	private AutonomicManager parentManager;
	
	public HostAutonomicManager(Host host, AutonomicManager parentManager) {
		this.host = host;
		this.parentManager = parentManager;
	}
	
	public Host getHost() {
		return host;
	}
	
	public AutonomicManager getParentManager() {
		return parentManager;
	}
	
}
