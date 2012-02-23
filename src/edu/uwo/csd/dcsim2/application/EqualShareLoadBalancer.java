package edu.uwo.csd.dcsim2.application;

import java.util.ArrayList;
import java.util.Map;

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
