package edu.uwo.csd.dcsim2.application;

import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.vm.VirtualResources;

public abstract class InteractiveApplication implements Application {

	protected VirtualResources resourcesRequired;
	private double workRemaining = 0;
	private ApplicationTier applicationTier;
	private VirtualResources overhead; //the amount of overhead per second this application creates
	private VirtualResources overheadRemaining; //the amount of overhead accumulated over the elapsed period that remains to be processed
	
	public InteractiveApplication(ApplicationTier applicationTier) {
		resourcesRequired = null;
		this.applicationTier = applicationTier;
		overhead = new VirtualResources(); //no overhead, by default
	}
	
	/*
	 * Called once at the beginning of scheduling
	 */
	public void beginScheduling() {
		//calculate overhead for scheduling period
		overheadRemaining = new VirtualResources();
		
		long elapsedTime = Simulation.getSimulation().getElapsedTime();
		overheadRemaining.setCpu(overhead.getCpu() * (elapsedTime / 1000.0));
		overheadRemaining.setBandwidth(overhead.getBandwidth() * (elapsedTime / 1000.0));
		overheadRemaining.setMemory(overhead.getMemory());
		overheadRemaining.setStorage(overhead.getStorage());
	}
	
	/*
	 * Called continuously while scheduling, before each time the VM is run
	 */
	private VirtualResources updateResourcesRequired() {
		double incomingWork = applicationTier.retrieveWork(this);
		if (resourcesRequired == null)
			resourcesRequired = new VirtualResources();
		if (incomingWork > 0) {
			resourcesRequired = resourcesRequired.add(calculateRequiredResources(incomingWork));
			workRemaining += incomingWork;
		}

		return resourcesRequired;
	}
	
	/*
	 * Called once at the end of scheduling
	 */
	public void completeScheduling() {
		//TODO record resourcesRequired not met
		
		//clear work remaining and resources required
		workRemaining = 0;
		resourcesRequired = null;
		
	}
	
	public VirtualResources runApplication(VirtualResources resourcesAvailable) {
		
		updateResourcesRequired();
		
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
		
		//check minimum memory and storage
		if (resourcesAvailable.getMemory() < overheadRemaining.getMemory() || resourcesAvailable.getStorage() < overheadRemaining.getStorage()) {
			//TODO log
			return new VirtualResources(); //no resources consumed
		}
		
		//next, we can process actual work
		CompletedWork completedWork = performWork(resourcesAvailable, workRemaining);
		
		if (completedWork.getWorkCompleted() > workRemaining)
			throw new RuntimeException("Application class " + this.getClass().getName() + " performed more work than was available to perform. Programming error.");
		
		applicationTier.getWorkTarget().addWork(completedWork.getWorkCompleted());
		workRemaining -= completedWork.getWorkCompleted();
		resourcesRequired = resourcesRequired.subtract(completedWork.getResourcesConsumed());
		
		//return consumed resources
		resourcesConsumed = resourcesConsumed.add(completedWork.resourcesConsumed);
		return resourcesConsumed;
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
	public double getResourcesRequired() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getResourcesInUse() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTotalResourcesRequired() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTotalResourcesConsumed() {
		// TODO Auto-generated method stub
		return 0;
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
