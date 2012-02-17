package edu.uwo.csd.dcsim2.application;

import java.util.*;

public abstract class LoadBalancer {

	ApplicationTier applicationTier;
	Map<Application, Integer> applicationWorkPending;
	int incomingWork;
	
	public LoadBalancer(ApplicationTier applicationTier) {
		this.applicationTier = applicationTier;
		applicationWorkPending = new HashMap<Application, Integer>();
		incomingWork = 0;
	}
	
	public void addWork(int work) {
		/**queue incoming work. Delay distributing it to applications until an application
		requests work, in order to reduce calls to distributeWork()
		*/
		incomingWork += work;
	}
	
	public abstract Map<Application, Integer> distributeWork(int work, Map<Application, Integer> applicationWorkPending);
	
	public int retrieveWork(Application application) {
		//distribute any queued incoming work
		if (incomingWork > 0) {
			applicationWorkPending = distributeWork(incomingWork, applicationWorkPending);
			incomingWork = 0;
		}
		
		int out = applicationWorkPending.get(application);
		applicationWorkPending.remove(application);
		return out;
	}
	
	public ApplicationTier getApplicationTier() {
		return applicationTier;
	}
	
}
