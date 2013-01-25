package edu.uwo.csd.dcsim.application.loadbalancer;

import java.util.*;

import edu.uwo.csd.dcsim.application.Application;
import edu.uwo.csd.dcsim.application.ApplicationTier;

/**
 * Represents a load balancer, splitting incoming workload between Applications in an ApplicationTier
 * 
 * @author Michael Tighe
 *
 */
public abstract class LoadBalancer {

	ApplicationTier applicationTier;
	Map<Application, Double> applicationWorkLevel;
	
	public LoadBalancer() {
		applicationWorkLevel = new HashMap<Application, Double>();
	}
	
	public abstract Map<Application, Double> distributeWork(double work);
	
	public double getWorkLevel(Application application) {
		
		double workLevel = applicationTier.getWorkSource().getWorkOutputLevel();

		//calculate work distribution
		applicationWorkLevel = distributeWork(workLevel);
		
		if (!applicationWorkLevel.containsKey(application))
			throw new RuntimeException("Application not found in load balancer");
		
		double out = applicationWorkLevel.get(application);

		return out;
	}
	
	public ApplicationTier getApplicationTier() {
		return applicationTier;
	}
	
	public void setApplicationTier(ApplicationTier applicationTier) {
		this.applicationTier = applicationTier;
	}
	
}
