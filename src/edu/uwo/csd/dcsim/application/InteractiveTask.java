package edu.uwo.csd.dcsim.application;

import java.util.ArrayList;

import edu.uwo.csd.dcsim.application.loadbalancer.*;
import edu.uwo.csd.dcsim.common.ObjectBuilder;
import edu.uwo.csd.dcsim.host.Resources;

/**
 * A tier of InteractiveApplications 
 * 
 * @author Michael Tighe
 *
 */
public class InteractiveTask extends Task {

	private LoadBalancer loadBalancer;
	private double normalServiceTime;
	private double visitRatio;
	private InteractiveApplication application;
	private ArrayList<InteractiveTaskInstance> instances = new ArrayList<InteractiveTaskInstance>();
	private long nextInstanceId = 1;
	
	public InteractiveTask(InteractiveApplication application,
			int defaultInstances,
			int maxInstances,
			Resources resourceSize,
			double normalServiceTime,
			double visitRatio) {
		super(defaultInstances, maxInstances, resourceSize);

		this.application = application;
		this.normalServiceTime = normalServiceTime;
		this.visitRatio = visitRatio;
		
		//set default load balancer
		setLoadBalancer(new EqualShareLoadBalancer());
	}
	
	public InteractiveTask(InteractiveApplication application,
			int defaultInstances,
			int maxInstances,
			Resources resourceSize,
			double normalServiceTime,
			double visitRatio,
			LoadBalancer loadBalancer) {
		super(defaultInstances, maxInstances, resourceSize);

		this.application = application;
		this.normalServiceTime = normalServiceTime;
		this.visitRatio = visitRatio;
		setLoadBalancer(loadBalancer);
	}
	
	public InteractiveTask(Builder builder) {
		super(builder.defaultInstances, builder.maxInstances, builder.resourceSize);
		
		this.application = builder.application;
		this.normalServiceTime = builder.serviceTime;
		this.visitRatio = builder.visitRatio;
		
		if (builder.loadBalancer == null) {
			//set default load balancer
			setLoadBalancer(new EqualShareLoadBalancer());
		} else {
			setLoadBalancer(builder.loadBalancer.build());
		}
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
		loadBalancer.setTask(this);
	}
	
	@Override
	public TaskInstance createInstance() {
		InteractiveTaskInstance instance = new InteractiveTaskInstance(this);
		instances.add(instance);
		instance.setId(nextInstanceId++);
		startInstance(instance);
		
		for (ApplicationListener listener : application.getApplicationListeners()) {
			listener.onCreateTaskInstance(instance);
		}
		
		return instance;
	}

	@Override
	public void removeInstance(TaskInstance instance) {
		if (instances.contains(instance)) {
			instances.remove(instance);
			stopInstance(instance);
			
			for (ApplicationListener listener : application.getApplicationListeners()) {
				listener.onRemoveTaskInstance(instance);
			}
			
		} else {
			throw new RuntimeException("Attempted to remove instance from task that does not contain it");
		}
	}

	@Override
	public void startInstance(TaskInstance instance) {
		//ensure that workload is started
		application.getWorkload().setEnabled(true);
	}

	@Override
	public void stopInstance(TaskInstance instance) {
		// TODO ...remove from Task/Load Balancer? When is this even used?
		
	}
	
	public double getNormalServiceTime() {
		return normalServiceTime;
	}
	
	public double getVisitRatio() {
		return visitRatio;
	}

	@Override
	public Application getApplication() {
		return application;
	}
	
	public void setApplication(InteractiveApplication application) {
		this.application = application;
	}

	public ArrayList<InteractiveTaskInstance> getInteractiveTaskInstances() {
		return instances;
	}

	@Override
	public ArrayList<TaskInstance> getInstances() {
		ArrayList<TaskInstance> simpleInstances = new ArrayList<TaskInstance>(instances);
		return simpleInstances;
	}
	
	public static class Builder implements ObjectBuilder<InteractiveTask> {

		int defaultInstances;
		int maxInstances;
		Resources resourceSize;
		double serviceTime; 
		double visitRatio;
		ObjectBuilder<LoadBalancer> loadBalancer = null;
		InteractiveApplication application = null;
		
		public Builder(int defaultInstances,
				int maxInstances,
				Resources resourceSize,
				double serviceTime, 
				double visitRatio) {
		
			this.defaultInstances = defaultInstances;
			this.maxInstances = maxInstances;
			this.resourceSize = resourceSize;
			this.serviceTime = serviceTime;
			this.visitRatio = visitRatio;			
		}
		
		public Builder loadBalancer(ObjectBuilder<LoadBalancer> loadBalancer) {
			this.loadBalancer = loadBalancer;
			return this;
		}
		
		public Builder application(InteractiveApplication application) {
			this.application = application;
			return this;
		}
		
		@Override
		public InteractiveTask build() {
			return new InteractiveTask(this);
		}
		
	}
	
}
