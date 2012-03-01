package edu.uwo.csd.dcsim2.application;

import edu.uwo.csd.dcsim2.vm.*;

public abstract class Application {

	protected VirtualResources resourcesRequired;
	private double workRemaining = 0;
	private ApplicationTier applicationTier;
	
	public Application(ApplicationTier applicationTier) {
		resourcesRequired = null;
		this.applicationTier = applicationTier;
	}
	
	public VirtualResources updateResourcesRequired() {
		double incomingWork = applicationTier.retrieveWork(this);
		if (resourcesRequired == null)
			resourcesRequired = new VirtualResources();
		if (incomingWork > 0) {
			resourcesRequired = resourcesRequired.add(calculateRequiredResources(incomingWork));
			workRemaining += incomingWork;
		}

		return resourcesRequired;
	}
	
	public VirtualResources processWork(VirtualResources resourcesAvailable) {
			
		CompletedWork completedWork = performWork(resourcesAvailable, workRemaining);
		
		if (completedWork.getWorkCompleted() > workRemaining)
			throw new RuntimeException("Application class " + this.getClass().getName() + " performed more work than was available to perform. Programming error.");
		
		applicationTier.getWorkTarget().addWork(completedWork.getWorkCompleted());
		workRemaining -= completedWork.getWorkCompleted();
		resourcesRequired = resourcesRequired.subtract(completedWork.getResourcesConsumed());
		
		//return consumed resources
		return completedWork.resourcesConsumed;
	}
	
	protected abstract VirtualResources calculateRequiredResources(double work);
	protected abstract CompletedWork performWork(VirtualResources resourcesAvailable, double workRemaining);
	
	public VirtualResources getResourcesRequired() {
		return resourcesRequired;
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
