package edu.uwo.csd.dcsim2.application;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.host.Host;
import edu.uwo.csd.dcsim2.vm.VirtualResources;

public abstract class InteractiveApplication extends Application {

	private static Logger logger = Logger.getLogger(Host.class);
	
	//variable to keep track of resource demand and consumption
	protected VirtualResources resourceDemand;		//the current level of resource demand / second
	protected VirtualResources resourceInUse;		//the current level of resource use  / second
	protected VirtualResources totalResourceDemand;	//the total amount of resources required since the application started
	protected VirtualResources totalResourceUsed;	//the total amount of resources used since the application started

	private double workRemaining = 0;
	private ApplicationTier applicationTier;
	private VirtualResources overhead; //the amount of overhead per second this application creates
	private VirtualResources overheadRemaining; //the amount of overhead accumulated over the elapsed period that remains to be processed
	
	public InteractiveApplication(ApplicationTier applicationTier) {
		
		//initialize resource demand/consumption values
		resourceDemand = new VirtualResources();
		resourceInUse = new VirtualResources();
		totalResourceDemand = new VirtualResources();
		totalResourceUsed = new VirtualResources();
		
		this.applicationTier = applicationTier;
		overhead = new VirtualResources(); //no overhead, by default
	}
	
	/*
	 * Called once at the beginning of scheduling
	 */
	public void beginScheduling() {
		//reset the resource demand and consumption values for the current interval
		resourceDemand = new VirtualResources();
		resourceInUse = new VirtualResources();
		
		//calculate overhead for scheduling period
		overheadRemaining = new VirtualResources();
		
		long elapsedTime = Simulation.getSimulation().getElapsedTime();
		overheadRemaining.setCpu(overhead.getCpu() * (elapsedTime / 1000.0));
		overheadRemaining.setBandwidth(overhead.getBandwidth() * (elapsedTime / 1000.0));
		overheadRemaining.setMemory(overhead.getMemory());
		overheadRemaining.setStorage(overhead.getStorage());
		
		//application overhead is included in resourceDemand
		resourceDemand = resourceDemand.add(overheadRemaining);
	}

	public void updateResourceDemand() {
		//retrieve incoming work
		double incomingWork = applicationTier.retrieveWork(this);
		workRemaining += incomingWork;
		
		//if there is incoming work, calculate the resources required to perform it and add it to resourceDemand
		if (incomingWork > 0) {
			resourceDemand = resourceDemand.add(calculateRequiredResources(incomingWork));
		}
	}
	
	public VirtualResources runApplication(VirtualResources resourcesAvailable) {

		VirtualResources resourcesConsumed = new VirtualResources();
		
		//first ensure that all remaining overhead for the elapsed period has been processed
		if (overheadRemaining.getCpu() > 0) {
			if (resourcesAvailable.getCpu() > overheadRemaining.getCpu()) {
				//we have enough cpu to complete processing the overhead
				resourcesAvailable.setCpu(resourcesAvailable.getCpu() - overheadRemaining.getCpu());
				resourcesConsumed.setCpu(overheadRemaining.getCpu());
				overheadRemaining.setCpu(0);
			} else {
				//we do not have enough cpu to complete processing the overhead
				overheadRemaining.setCpu(overheadRemaining.getCpu() - resourcesAvailable.getCpu());
				resourcesConsumed.setCpu(resourcesAvailable.getCpu());
				resourcesAvailable.setCpu(0);
			}
		}
		if (overheadRemaining.getBandwidth() > 0) {
			if (resourcesAvailable.getBandwidth() > overheadRemaining.getBandwidth()) {
				//we have enough bandwidth to complete processing the overhead
				resourcesAvailable.setBandwidth(resourcesAvailable.getBandwidth() - overheadRemaining.getBandwidth());
				resourcesConsumed.setBandwidth(overheadRemaining.getBandwidth());
				overheadRemaining.setBandwidth(0);
			} else {
				//we do not have enough bandwidth to complete processing the overhead
				overheadRemaining.setBandwidth(overheadRemaining.getBandwidth() - resourcesAvailable.getBandwidth());
				resourcesConsumed.setBandwidth(resourcesAvailable.getBandwidth());
				resourcesAvailable.setBandwidth(0);
			}
		}
		
		resourcesConsumed.setMemory(overheadRemaining.getMemory());
		resourcesConsumed.setStorage(overheadRemaining.getStorage());
		
		//check minimum memory and storage. If not met, assume the application does not run. TODO is this correct? Should we use what we can? How would this affect application performance?
		if (resourcesAvailable.getMemory() < overheadRemaining.getMemory() || resourcesAvailable.getStorage() < overheadRemaining.getStorage()) {
			logger.info("Application has insufficient memory or storage to meet overhead requirements");
			return new VirtualResources(); //no resources consumed
		}
		
		//next, we can process actual work
		CompletedWork completedWork = performWork(resourcesAvailable, workRemaining);
		
		if (completedWork.getWorkCompleted() > workRemaining)
			throw new RuntimeException("Application class " + this.getClass().getName() + " performed more work than was available to perform. Programming error.");
		
		applicationTier.getWorkTarget().addWork(completedWork.getWorkCompleted());
		workRemaining -= completedWork.getWorkCompleted();
	
		//compute total consumed resources
		resourcesConsumed = resourcesConsumed.add(completedWork.resourcesConsumed);
		
		//add resourcesConsumed to resourcesInUse, which is keeping track of all resources used during this time interval
		resourceInUse = resourceInUse.add(resourcesConsumed);
		
		return resourcesConsumed;
	}
	
	/*
	 * Called once at the end of scheduling
	 */
	public void completeScheduling() {

		//add resource demand and use for this time interval to total values
		totalResourceDemand = totalResourceDemand.add(resourceDemand);
		totalResourceUsed = totalResourceUsed.add(resourceInUse);
		
		//convert resourceDemand and resourceInUse to a 'resource per second' value by dividing by seconds elapsed in time interval
		resourceDemand.setCpu(resourceDemand.getCpu() / (Simulation.getSimulation().getElapsedTime() / 1000d));
		resourceDemand.setBandwidth(resourceDemand.getBandwidth() / (Simulation.getSimulation().getElapsedTime() / 1000d));
		
		resourceInUse.setCpu(resourceInUse.getCpu() / (Simulation.getSimulation().getElapsedTime() / 1000d));
		resourceInUse.setBandwidth(resourceInUse.getBandwidth() / (Simulation.getSimulation().getElapsedTime() / 1000d));
		
		//clear work remaining (i.e. drop requests that could not be fulfilled)
		workRemaining = 0;
	}
	
	@Override
	public void updateMetrics() {
		globalResourceDemand = globalResourceDemand.add(resourceDemand);
		globalResourceUsed = globalResourceUsed.add(resourceInUse);
	}
	
	protected abstract VirtualResources calculateRequiredResources(double work);
	protected abstract CompletedWork performWork(VirtualResources resourcesAvailable, double workRemaining);
	
	public VirtualResources getOverhead() {
		return overhead;
	}
	
	public void setOverhead(VirtualResources overhead) {
		this.overhead = overhead;
	}
	
	@Override
	public VirtualResources getResourceDemand() {
		return resourceDemand;
	}

	@Override
	public VirtualResources getResourceInUse() {
		return resourceInUse;
	}

	@Override
	public VirtualResources getTotalResourceDemand() {
		return totalResourceDemand;
	}

	@Override
	public VirtualResources getTotalResourceUsed() {
		return totalResourceUsed;
	}
	
	protected class CompletedWork {
		
		private double workCompleted;
		private VirtualResources resourcesConsumed;
		
		public CompletedWork(double workCompleted, VirtualResources resourcesConsumed) {
			this.workCompleted = workCompleted;
			this.resourcesConsumed = resourcesConsumed;
		}
		
		public double getWorkCompleted() {
			return workCompleted;
		}
		
		public VirtualResources getResourcesConsumed() {
			return resourcesConsumed;
		}
		
	}
	
}
