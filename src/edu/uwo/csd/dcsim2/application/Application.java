package edu.uwo.csd.dcsim2.application;

import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.core.metrics.*;
import edu.uwo.csd.dcsim2.vm.*;

public abstract class Application {

	public static final String SLA_VIOLATION_METRIC = "slaViolation";
	public static final String SLA_VIOLATION_UNDERPROVISION_METRIC = "slaViolationUnderprovision";
	public static final String SLA_VIOLATION_MIGRATION_OVERHEAD_METRIC = "slaViolationMigOverhead"; 
	
	protected VM vm;
	protected Simulation simulation;
	
	public Application(Simulation simulation) {
		this.simulation = simulation;
		
		FractionalMetric.getSimulationMetric(simulation, SLA_VIOLATION_METRIC).initializeOutputFormatter(new PercentageFormatter());
		FractionalMetric.getSimulationMetric(simulation, SLA_VIOLATION_UNDERPROVISION_METRIC).initializeOutputFormatter(new PercentageFormatter());
		FractionalMetric.getSimulationMetric(simulation, SLA_VIOLATION_MIGRATION_OVERHEAD_METRIC).initializeOutputFormatter(new PercentageFormatter());
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
	public abstract void prepareExecution();
	
	public abstract void updateResourceDemand();
	
	public abstract VirtualResources execute(VirtualResources resourcesAvailable);

	/*
	 * Called once at the end of scheduling
	 */
	public abstract void completeExecution();
	
	public abstract double getSLAViolation();
	public abstract double getTotalSLAViolation();
	public abstract double getSLAViolatedWork();
	public abstract double getTotalSLAViolatedWork();
	public abstract double getMigrationPenalty();
	public abstract double getTotalMigrationPenalty();
	
	public abstract VirtualResources getResourceDemand();
	public abstract VirtualResources getResourceInUse();
	public abstract VirtualResources getTotalResourceDemand();
	public abstract VirtualResources getTotalResourceUsed();
	
	public abstract void updateMetrics();
	
	
}
