package edu.uwo.csd.dcsim2.application;

import java.util.ArrayList;
import java.util.Map;

public class EqualShareLoadBalancer extends LoadBalancer {

	public EqualShareLoadBalancer(ApplicationTier applicationTier) {
		super(applicationTier);
	}

	@Override
	public Map<Application, Integer> distributeWork(int work, Map<Application, Integer> applicationWorkPending) {
		int workPerApp;
		int remainder;
		ArrayList<Application> applications = this.getApplicationTier().getApplications();
		
		if (applications.size() > 0) {
			workPerApp = (int)Math.floor(work / applications.size());
			remainder = work - (workPerApp * applications.size());
			
			for (Application app : applications) {
				int newWork = workPerApp;
				
				//TODO this is flawed: the first applications in the list will receive a few more units of work than the rest
				if (remainder > 0) {
					newWork += 1;
					--remainder;
				}
				
				if (applicationWorkPending.containsKey(app)) {
					newWork += applicationWorkPending.get(app);
				}
				
				applicationWorkPending.put(app, newWork);
			}
			
		}
		
		return applicationWorkPending;
	}

}
