package edu.uwo.csd.dcsim2.application;

import java.util.ArrayList;

public abstract class ApplicationTier implements WorkConsumer {

	private WorkConsumer workTarget;
	private LoadBalancer loadBalancer;
	private ArrayList<Application> applications = new ArrayList<Application>();
	private int incomingWork = 0; //used in the case of no load balancer being present
	
	public Application createApplication() {
		Application newApp = instantiateApplication();
		this.applications.add(newApp);
		
		return newApp;
	}
	
	public void removeApplication(Application application) {
		this.applications.remove(application);
	}
	
	protected abstract Application instantiateApplication();

	public int getHeight() {
		int height = 1;
		
		WorkConsumer child = workTarget;
		while (child instanceof ApplicationTier) {
			++height;
			child = ((ApplicationTier)child).getWorkTarget();
		}
		return height;
	}
	
	public int retrieveWork(Application application) {
		if (loadBalancer != null) {
			return loadBalancer.retrieveWork(application);
		} else {
			int work = incomingWork;
			incomingWork = 0;
			return work;
		}
	}

	public ArrayList<Application> getApplications() {
		return applications;
	}
	
	public LoadBalancer getLoadBalancer() {
		return loadBalancer;
	}
	
	public void setLoadBalancer(LoadBalancer loadBalancer) {
		this.loadBalancer = loadBalancer;
		loadBalancer.setApplicationTier(this);
	}
	
	public WorkConsumer getWorkTarget() {
		return workTarget;
	}
	
	public void setWorkTarget(WorkConsumer workTarget) {
		this.workTarget = workTarget;
	}
	
	@Override
	public void addWork(int work) {
		if (loadBalancer != null) {
			loadBalancer.addWork(work);
		} else {
			incomingWork += work;
		}
	}
	
}
