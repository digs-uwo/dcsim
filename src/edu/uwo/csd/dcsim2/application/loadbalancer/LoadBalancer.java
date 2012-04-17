package edu.uwo.csd.dcsim2.application.loadbalancer;

import java.util.*;

import edu.uwo.csd.dcsim2.application.Application;
import edu.uwo.csd.dcsim2.application.ApplicationTier;

public abstract class LoadBalancer {

	ApplicationTier applicationTier;
	Map<Application, Double> applicationWorkPending;
	float incomingWork;
	
	public LoadBalancer() {
		applicationWorkPending = new HashMap<Application, Double>();
		incomingWork = 0;
	}
	
	public void addWork(double work) {
		/**queue incoming work. Delay distributing it to applications until an application
		requests work, in order to reduce calls to distributeWork()
		*/
		incomingWork += work;
	}
	
	public abstract Map<Application, Double> distributeWork(double work, Map<Application, Double> applicationWorkPending);
	
	public double retrieveWork(Application application) {
		//distribute any queued incoming work
		
		applicationWorkPending = distributeWork(incomingWork, applicationWorkPending);
		incomingWork = 0;
		
		if (!applicationWorkPending.containsKey(application))
			throw new RuntimeException("Application not found in load balancer pending work");
		
		double out = applicationWorkPending.get(application);
		applicationWorkPending.remove(application);
		return out;
	}
	
	public ApplicationTier getApplicationTier() {
		return applicationTier;
	}
	
	public void setApplicationTier(ApplicationTier applicationTier) {
		this.applicationTier = applicationTier;
	}
	
}
