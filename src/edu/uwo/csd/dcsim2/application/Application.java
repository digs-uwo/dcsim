package edu.uwo.csd.dcsim2.application;

import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.core.Utility;
import edu.uwo.csd.dcsim2.vm.*;

public abstract class Application {

	protected static VirtualResources globalResourceDemand = new VirtualResources();
	protected static VirtualResources globalResourceUsed = new VirtualResources();
	protected static double globalIncomingWork = 0;
	protected static double globalSlaViolatedWork = 0;

	protected VM vm;
	protected Simulation simulation;
	
	public Application(Simulation simulation) {
		this.simulation = simulation;
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
	
	public abstract double getSLAViolation();
	public abstract double getTotalSLAViolation();
	public abstract double getSLAViolatedWork();
	public abstract double getTotalSLAViolatedWork();
	
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
	
	public static double getGlobalSLAViolation() {
		return Utility.roundDouble((globalSlaViolatedWork / globalIncomingWork) * 100, 3); 
	}
	
}
