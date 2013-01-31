package edu.uwo.csd.dcsim.application;

import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.host.Resources;
import edu.uwo.csd.dcsim.vm.*;

/**
 * Represents an application being run on a VM
 * 
 * @author Michael Tighe
 *
 */
public abstract class Application implements WorkProducer {

	public static final String SLA_VIOLATION_METRIC = "slaViolation"; //name of the metric collection the SLA violation
	public static final String SLA_VIOLATION_UNDERPROVISION_METRIC = "slaViolationUnderprovision"; //name of the metric collecting SLA violation due to under provisioning
	public static final String SLA_VIOLATION_MIGRATION_OVERHEAD_METRIC = "slaViolationMigOverhead"; //name of the metric collection SLA violation due to migration overhead
	
	protected VM vm; //the VM on which this application is running
	protected Simulation simulation; //the simulation this Application is running within
	protected Resources resourcesRequired;
	
	/**
	 * Create a new Application, attached to the specified Simulation
	 * 
	 * @param simulation the simulation in which this Application is to run
	 */
	public Application(Simulation simulation) {
		this.simulation = simulation;
		this.resourcesRequired = new Resources();
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
	 * Get the current amount of resource required based on the work level from tha application source
	 * @return
	 */
	public final Resources getResourcesRequired() {
		return resourcesRequired;
	}
	
	/**
	 * Update the required resources based on current work levels
	 */
	protected abstract Resources calculateResourcesRequired();
	
	public final void updateResourceRequirements() {
		resourcesRequired = calculateResourcesRequired();
	}
	
	/**
	 * Schedule resources and update the work output level of the application
	 * @param resourcesScheduled
	 */
	public abstract void scheduleResources(Resources resourcesScheduled);

	/**
	 * Called after scheduling but before advancing to the next simulation time (executing), offering
	 * an opportunity to trigger future events
	 */
	public abstract void postScheduling();
	
	/**
	 * Executes the application up to the current simulation time
	 */
	public abstract void execute();

	/**
	 * Update simulation metrics
	 */
	public abstract void updateMetrics();
	
	public abstract Resources getResourcesInUse();
	
	public abstract double getSLAUnderprovisionRate();
	public abstract double getSLAMigrationPenaltyRate();
	public abstract double getTotalIncomingWork();
	public abstract double getTotalSLAViolatedWork();
	
	
}
