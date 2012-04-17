package edu.uwo.csd.dcsim2.application;

import edu.uwo.csd.dcsim2.vm.*;

public abstract class Application {

	protected static VirtualResources globalResourceDemand = new VirtualResources();
	protected static VirtualResources globalResourceUsed = new VirtualResources();

	protected VM vm;
	
	public Application() {
	}
	
	public VM getVM() {
		return vm;
	}
	
	public void setVM(VM vm) {
		this.vm = vm;
	}
	
	/*
	 * Called once at the beginning of scheduling
	 */
	public abstract void beginScheduling();
	
	public abstract void updateResourceDemand();
	
	public abstract VirtualResources runApplication(VirtualResources resourcesAvailable);
	
	/*
	 * Called once at the end of scheduling
	 */
	public abstract void completeScheduling();
	
	

	public abstract VirtualResources getResourceDemand();
	public abstract VirtualResources getResourceInUse();
	public abstract VirtualResources getTotalResourceDemand();
	public abstract VirtualResources getTotalResourceUsed();
	
	public abstract void updateMetrics();
	
	public static VirtualResources getGlobalResourceDemand() {
		return globalResourceDemand;
	}
	
	public static VirtualResources getGlobalResourceUsed() {
		return globalResourceUsed;
	}
	
}
