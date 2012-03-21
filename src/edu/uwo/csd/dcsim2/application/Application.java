package edu.uwo.csd.dcsim2.application;

import edu.uwo.csd.dcsim2.vm.*;

public interface Application {

	/*
	 * Called once at the beginning of scheduling
	 */
	public void beginScheduling();
	
	/*
	 * Called once at the end of scheduling
	 */
	public void completeScheduling();
	
	public VirtualResources runApplication(VirtualResources resourcesAvailable);

	public VirtualResources getResourceDemand();
	public VirtualResources getResourceInUse();
	public VirtualResources getTotalResourceDemand();
	public VirtualResources getTotalResourceUsed();
	
}
