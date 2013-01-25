package edu.uwo.csd.dcsim.application.loadbalancer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.uwo.csd.dcsim.application.Application;

/**
 * EqualShareLoadBalancer is a LoadBalancer that splits incoming work equal among all Applications in an
 * ApplicationTier
 * 
 * @author Michael Tighe
 *
 */
public class EqualShareLoadBalancer extends LoadBalancer {

	@Override
	public Map<Application, Double> distributeWork(double work) {
		double workPerApp;

		Map<Application, Double> applicationWorkLevel = new HashMap<Application, Double>();
		
		ArrayList<Application> applications = this.getApplicationTier().getApplications();
		
		if (applications.size() > 0) {
			workPerApp = work / applications.size();
			
			for (Application app : applications) {
				applicationWorkLevel.put(app, workPerApp);
			}
			
		}
		
		return applicationWorkLevel;
	}

}
