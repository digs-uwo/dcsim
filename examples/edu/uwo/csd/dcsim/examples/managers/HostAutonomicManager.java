package edu.uwo.csd.dcsim.examples.managers;

import edu.uwo.csd.dcsim.management.*;

public class HostAutonomicManager extends AutonomicManager {

	private AutonomicManager parentManager;
	
	public HostAutonomicManager() {

	}
	
	public void installPolicy(HostPolicy policy) {
		super.installPolicy(policy);
	}
	
	
	public void uninstallPolicy(HostPolicy policy) {
		super.uninstallPolicy(policy);
	}
	
	public AutonomicManager getParentManager() {
		return parentManager;
	}
	
}
