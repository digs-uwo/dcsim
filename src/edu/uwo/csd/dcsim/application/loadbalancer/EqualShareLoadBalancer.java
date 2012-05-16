package edu.uwo.csd.dcsim.application.loadbalancer;

import java.util.ArrayList;
import java.util.Map;

import edu.uwo.csd.dcsim.application.Application;

public class EqualShareLoadBalancer extends LoadBalancer {

	@Override
	public Map<Application, Double> distributeWork(double work, Map<Application, Double> applicationWorkPending) {
		double workPerApp;
		ArrayList<Application> applications = this.getApplicationTier().getApplications();
		
		if (applications.size() > 0) {
			workPerApp = work / applications.size();
			
			for (Application app : applications) {
				double newWork = workPerApp;

				if (applicationWorkPending.containsKey(app)) {
					newWork += applicationWorkPending.get(app);
				}
				
				applicationWorkPending.put(app, newWork);
			}
			
		}
		
		return applicationWorkPending;
	}

}
