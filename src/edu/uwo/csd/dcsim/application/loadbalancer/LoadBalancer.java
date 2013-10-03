package edu.uwo.csd.dcsim.application.loadbalancer;

import edu.uwo.csd.dcsim.application.TaskInstance;
import edu.uwo.csd.dcsim.application.Task;

/**
 * Represents a load balancer, splitting incoming workload between Applications in an ApplicationTier
 * 
 * @author Michael Tighe
 *
 */
public abstract class LoadBalancer {

	protected Task task;
	
	public abstract float getInstanceShare(TaskInstance taskInstance);
	
	public Task getTask() {
		return task;
	}
	
	public void setTask(Task task) {
		this.task = task;
	}
	
}
