package edu.uwo.csd.dcsim2.application;

import java.util.ArrayList;

public abstract class ApplicationTier implements WorkConsumer {

	private WorkConsumer workTarget;
	private LoadBalancer loadBalancer;
	private ArrayList<Application> applications;
	
	public ApplicationTier(WorkConsumer workTarget, LoadBalancer loadBalancer) {
		this.workTarget = workTarget;
		this.loadBalancer = loadBalancer;
		applications = new ArrayList<Application>();
	}

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

	public ArrayList<Application> getApplications() {
		return applications;
	}
	
	public LoadBalancer getLoadBalancer() {
		return loadBalancer;
	}
	
	public WorkConsumer getWorkTarget() {
		return workTarget;
	}
	
	@Override
	public void addWork(int work) {
		loadBalancer.addWork(work);
	}
	
}
