package edu.uwo.csd.dcsim2.application;

import java.util.*;

public abstract class LoadBalancer {

	ApplicationTier applicationTier;
	Map<Application, Integer> applicationWorkPending;
	
	public LoadBalancer(ApplicationTier applicationTier) {
		this.applicationTier = applicationTier;
		applicationWorkPending = new HashMap<Application, Integer>();
	}
	
	public abstract void distributeWork(int work);
	
	public int retrieveWork(Application application) {
		int out = applicationWorkPending.get(application);
		applicationWorkPending.remove(application);
		return out;
	}
	
}
