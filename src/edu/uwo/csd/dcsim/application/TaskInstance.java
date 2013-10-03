package edu.uwo.csd.dcsim.application;

import edu.uwo.csd.dcsim.host.Resources;
import edu.uwo.csd.dcsim.vm.*;

/**
 * 
 * @author Michael Tighe
 *
 */
public abstract class TaskInstance  {

	protected long id = -1;
	protected Vm vm; //the VM on which this task is running
	protected Resources fullDemand;
	protected Resources resourceDemand;
	protected Resources resourceScheduled;
	
	/**
	 * Create a new Application, attached to the specified Simulation
	 * 
	 * @param simulation the simulation in which this Application is to run
	 */
	public TaskInstance() {
		this.fullDemand = new Resources();
		this.resourceDemand = new Resources();
		this.resourceScheduled = new Resources();
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	
	/**
	 * Get the VM that this Application is running in
	 * @return
	 */
	public Vm getVM() {
		return vm;
	}
	
	/**
	 * Set the VM that this Application is running in
	 * @param vm
	 */
	public void setVM(Vm vm) {
		this.vm = vm;
	}
	
	public final Resources getFullDemand() {
		return fullDemand;
	}
	
	public final Resources getResourceDemand() {
		return resourceDemand;
	}
	
	public final Resources getResourceScheduled() {
		return resourceScheduled;
	}
	
	public final void setFullDemand(Resources fullDemand) {
		this.fullDemand = fullDemand;
	}
	
	public final void setResourceDemand(Resources resourceDemand) {
		this.resourceDemand = resourceDemand;
	}
	
	public final void setResourceScheduled(Resources resourceScheduled) {
		this.resourceScheduled = resourceScheduled;
	}
	
	/**
	 * Called after scheduling but before advancing to the next simulation time (executing), offering
	 * an opportunity to trigger future events
	 */
	public abstract void postScheduling();
	
	public abstract Task getTask();
	
	@Override
	public abstract int hashCode();

}
