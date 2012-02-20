package edu.uwo.csd.dcsim2.application;

import edu.uwo.csd.dcsim2.vm.*;

public abstract class Application {

	protected VirtualResources resourcesRequired;
	private ApplicationTier applicationTier;
	
	public Application(ApplicationTier applicationTier) {
		resourcesRequired = new VirtualResources();
		this.applicationTier = applicationTier;
	}
	
	public VirtualResources updateResourcesRequired() {
		int incomingWork = applicationTier.retrieveWork(this);
		if (incomingWork > 0) {
			resourcesRequired = VirtualResources.add(resourcesRequired, convertWorkToResources(incomingWork));
		}
		return resourcesRequired;
	}
	
	public void processWork(VirtualResources resourcesAvailable) {
		int workComplete = convertResourcesToWork(resourcesAvailable);
		applicationTier.getWorkTarget().addWork(workComplete);
		
		resourcesRequired = VirtualResources.subtract(resourcesRequired, resourcesAvailable);
	}
	
	protected abstract VirtualResources convertWorkToResources(int work);
	protected abstract int convertResourcesToWork(VirtualResources resources);
	
	public VirtualResources getResourcesRequired() {
		return resourcesRequired;
	}

}
