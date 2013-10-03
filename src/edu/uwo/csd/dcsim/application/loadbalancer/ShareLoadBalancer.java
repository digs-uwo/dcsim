package edu.uwo.csd.dcsim.application.loadbalancer;

import java.util.HashMap;

import edu.uwo.csd.dcsim.application.TaskInstance;
import edu.uwo.csd.dcsim.common.ObjectBuilder;

/**
 * 
 * @author Michael Tighe
 *
 */
public class ShareLoadBalancer extends LoadBalancer {

	private HashMap<TaskInstance, Integer> instanceShares = new HashMap<TaskInstance, Integer>();
	
	public void setTaskInstanceShare(TaskInstance taskInstance, int shares) {
		instanceShares.put(taskInstance, shares);
	}
		
	@Override
	public float getInstanceShare(TaskInstance taskInstance) {
		
		int totalShare = 0;
		for (TaskInstance t : task.getInstances()) {
			if (!instanceShares.containsKey(t)) {
				instanceShares.put(t, task.getResourceSize().getCpu());
			}
			totalShare += instanceShares.get(t);
		}
		int taskShare = instanceShares.get(taskInstance);
		
		if (task == null) {
			throw new RuntimeException("Load Balancer called with null Task");
		}
		
		return taskShare / (float)totalShare;
	}

	public static class Builder implements ObjectBuilder<LoadBalancer> {

		@Override
		public LoadBalancer build() {
			return new ShareLoadBalancer();
		}
		
	}

}
