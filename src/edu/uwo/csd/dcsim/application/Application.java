package edu.uwo.csd.dcsim.application;

import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.core.metrics.*;
import edu.uwo.csd.dcsim.vm.*;

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
	
	/**
	 * Get the percentage of incoming work to this application for which SLA is violated
	 * at the current simulation time
	 * @return
	 */
	public abstract double getSLAViolation();
	
	/**
	 * Get the percentage of incoming work to this application for which SLA has been violated
	 * since the simulation began
	 * @return
	 */
	public abstract double getTotalSLAViolation();
	
	/**
	 * Get the amount of work (in units of work) for which SLA is violated at the current
	 * simulation time
	 * @return
	 */
	public abstract double getSLAViolatedWork();
	
	/**
	 * Get the amount of work (in units of work) for which SLA has been violated since
	 * the simulation began
	 * @return
	 */
	public abstract double getTotalSLAViolatedWork();
	
	/**
	 * Get the percentage of incoming work to this application for which SLA is violated
	 * due to VM migration involving this application at the current simulation time 
	 * @return
	 */
	public abstract double getMigrationPenalty();
	
	/**
	 * Get the percentage of incoming work to this application for which SLA has been violated
	 * due to VM migration involving this application since the simulation began
	 * @return
	 */
	public abstract double getTotalMigrationPenalty();
	
	public abstract VirtualResources getResourceDemand();
	public abstract VirtualResources getResourceInUse();
	public abstract VirtualResources getTotalResourceDemand();
	public abstract VirtualResources getTotalResourceUsed();
	
	public abstract void updateMetrics();
	
	
}
