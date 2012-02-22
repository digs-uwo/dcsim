package edu.uwo.csd.dcsim2.application;

import edu.uwo.csd.dcsim2.vm.*;

public abstract class Application {

	protected VirtualResources resourcesRequired;
	private ApplicationTier applicationTier;
	
	public Application(ApplicationTier applicationTier) {
		resourcesRequired = null;
		this.applicationTier = applicationTier;
	}
	
	public VirtualResources updateResourcesRequired() {
		int incomingWork = applicationTier.retrieveWork(this);
		if (incomingWork > 0) {
			if (resourcesRequired == null) {
				resourcesRequired = calculateRequiredResources(incomingWork);
			} else {
				resourcesRequired = resourcesRequired.add(calculateRequiredResources(incomingWork));
			}
		}

		return resourcesRequired;
	}
	
	public VirtualResources processWork(VirtualResources resourcesAvailable) {
		
		CompletedWork completedWork = performWork(resourcesAvailable);
		
		applicationTier.getWorkTarget().addWork(completedWork.getWorkCompleted());
		
		resourcesRequired = resourcesRequired.subtract(completedWork.getResourcesConsumed());
		
		//return consumed resources
		return completedWork.resourcesConsumed;
	}
	
	protected abstract VirtualResources calculateRequiredResources(int work);
	protected abstract CompletedWork performWork(VirtualResources resourcesAvailable);
	
	public VirtualResources getResourcesRequired() {
		return resourcesRequired;
	}
	
	protected class CompletedWork {
		
		private int workCompleted;
		private VirtualResources resourcesConsumed;
		
		public CompletedWork(int workCompleted, VirtualResources resourcesConsumed) {
			this.workCompleted = workCompleted;
			this.resourcesConsumed = resourcesConsumed;
		}
		
		public int getWorkCompleted() {
			return workCompleted;
		}
		
		public VirtualResources getResourcesConsumed() {
			return resourcesConsumed;
		}
		
	}

}
