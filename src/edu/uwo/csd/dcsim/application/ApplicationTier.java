package edu.uwo.csd.dcsim.application;

import java.util.ArrayList;

import edu.uwo.csd.dcsim.application.loadbalancer.LoadBalancer;
import edu.uwo.csd.dcsim.core.Simulation;

/**
 * ApplicationTier manages a collection of replica Applications sharing incoming workload. Applications
 * retrieve work from the tier, which may be load balanced between the collection of Applications in the
 * tier by a LoadBalancer object. ApplicationTier also acts as the ApplicationFactory for the Application
 * class that the tier consists of.
 * 
 * @author Michael Tighe
 *
 */
public abstract class ApplicationTier implements WorkConsumer, ApplicationFactory {

	private WorkConsumer workTarget; //the consumer to send completed work to
	private LoadBalancer loadBalancer; //handles balancing load between Applications in the tier
	private ArrayList<Application> applications = new ArrayList<Application>(); //the set of applications in the tier
	private double incomingWork = 0; //used in the case of no load balancer is present

	/**
	 * Create an Application instance for this tier
	 */
	public Application createApplication(Simulation simulation) {
		Application newApp = instantiateApplication(simulation); //create the application
		startApplication(newApp); //start the application (adds it to the tier)
		
		return newApp;
	}
	
	/**
	 * Call when starting an Application. Adds the Application to the tier.
	 */
	public void startApplication(Application application) {
		if (!applications.contains(application)) {
			applications.add(application);
		} else {
			throw new RuntimeException("Attempted to start an application that was already started");
		}
	}
	
	/**
	 * Call when stopping an Application. Removes the Application from the tier.
	 */
	public void stopApplication(Application application) {
		if(applications.contains(application)) {
			this.applications.remove(application);
		} else {
			throw new RuntimeException("Attempted to stop an application that was not part of the tier");
		}
	}
	
	/**
	 * Create a new instance of the Application
	 * @param simulation
	 * @return
	 */
	protected abstract Application instantiateApplication(Simulation simulation);

	@Override
	public int getHeight() {
		int height = 1;
		
		WorkConsumer child = workTarget;
		while (child instanceof ApplicationTier) {
			++height;
			child = ((ApplicationTier)child).getWorkTarget();
		}
		return height;
	}
	
	/**
	 * Get any incoming work waiting to be processed
	 * @param application
	 * @return
	 */
	public double retrieveWork(Application application) {
		if (loadBalancer != null) {
			return loadBalancer.retrieveWork(application);
		} else {
			double work = incomingWork;
			incomingWork = 0;
			return work;
		}
	}

	/**
	 * Get the collection of Applications in this tier 
	 * @return
	 */
	public ArrayList<Application> getApplications() {
		return applications;
	}
	
	/**
	 * Get the load balancer for this tier
	 * @return
	 */
	public LoadBalancer getLoadBalancer() {
		return loadBalancer;
	}
	
	/**
	 * Set the load balancer for this tier
	 * @param loadBalancer
	 */
	public void setLoadBalancer(LoadBalancer loadBalancer) {
		this.loadBalancer = loadBalancer;
		loadBalancer.setApplicationTier(this);
	}
	
	/**
	 * Get the target for completed work
	 * @return
	 */
	public WorkConsumer getWorkTarget() {
		return workTarget;
	}
	
	/**
	 * Set the target for completed work
	 * @param workTarget
	 */
	public void setWorkTarget(WorkConsumer workTarget) {
		this.workTarget = workTarget;
	}
	
	@Override
	public void addWork(double work) {
		//add incoming work to the load balancer, if present. Otherwise, add to incomingWork
		if (loadBalancer != null) {
			loadBalancer.addWork(work);
		} else {
			incomingWork += work;
		}
	}
	
}
