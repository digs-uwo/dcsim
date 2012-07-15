package edu.uwo.csd.dcsim.application;

import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.core.metrics.*;
import edu.uwo.csd.dcsim.vm.*;

/**
 * Represents an application being run on a VM
 * 
 * @author Michael Tighe
 *
 */
public abstract class Application {

	public static final String SLA_VIOLATION_METRIC = "slaViolation"; //name of the metric collection the SLA violation
	public static final String SLA_VIOLATION_UNDERPROVISION_METRIC = "slaViolationUnderprovision"; //name of the metric collecting SLA violation due to under provisioning
	public static final String SLA_VIOLATION_MIGRATION_OVERHEAD_METRIC = "slaViolationMigOverhead"; //name of the metric colleciton SLA violation due to migration overhead
	
	protected VM vm; //the VM on which this application is running
	protected Simulation simulation; //the simulation this Application is running within
	
	/**
	 * Create a new Application, attached to the specified Simulation
	 * 
	 * @param simulation the simulation in which this Application is to run
	 */
	public Application(Simulation simulation) {
		this.simulation = simulation;
		
		//initialize metric output formatters
		FractionalMetric.getSimulationMetric(simulation, SLA_VIOLATION_METRIC).initializeOutputFormatter(new PercentageFormatter());
		FractionalMetric.getSimulationMetric(simulation, SLA_VIOLATION_UNDERPROVISION_METRIC).initializeOutputFormatter(new PercentageFormatter());
		FractionalMetric.getSimulationMetric(simulation, SLA_VIOLATION_MIGRATION_OVERHEAD_METRIC).initializeOutputFormatter(new PercentageFormatter());
	}
	
	/**
	 * Get the VM that this Application is running in
	 * @return
	 */
	public VM getVM() {
		return vm;
	}
	
	/**
	 * Set the VM that this Application is running in
	 * @param vm
	 */
	public void setVM(VM vm) {
		this.vm = vm;
	}
	
	/**
	 * Called once at the beginning of VM execution. Use to set up object for execution 
	 */
	public abstract void prepareExecution();
	
	/**
	 * Update the resources required by this Application to fully meet its workload
	 */
	public abstract void updateResourceDemand();
	
	/**
	 * Run this Application with the specified amount of available resources
	 * @param resourcesAvailable The resources that may be consumed by the Application
	 * @return
	 */
	public abstract VirtualResources execute(VirtualResources resourcesAvailable);

	/**
	 * Called once at the end of VM execution
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
	
	/**
	 * Get the Applications current demand for resources
	 * @return
	 */
	public abstract VirtualResources getResourceDemand();
	
	/**
	 * Get the Applications current amount of resource in use
	 * @return
	 */
	public abstract VirtualResources getResourceInUse();
	
	/**
	 * Get the incoming work to the application
	 * @return
	 */
	public abstract double getIncomingWork();
	
	/**
	 * Get the total incoming work for the entire simulation
	 * @return
	 */
	public abstract double getTotalIncomingWork();
	
	/**
	 * Get the total resources demanded by this Application during the simulation
	 * @return
	 */
	public abstract VirtualResources getTotalResourceDemand();
	
	/**
	 * Get the total resources used by this Application during the simulation
	 * @return
	 */
	public abstract VirtualResources getTotalResourceUsed();
	
	/**
	 * Update simulation metrics
	 */
	public abstract void updateMetrics();
	
	
}
