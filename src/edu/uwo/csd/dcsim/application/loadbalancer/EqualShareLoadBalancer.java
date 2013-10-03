package edu.uwo.csd.dcsim.application.loadbalancer;

import edu.uwo.csd.dcsim.application.TaskInstance;
import edu.uwo.csd.dcsim.common.ObjectBuilder;

/**
 * 
 * @author Michael Tighe
 *
 */
public class EqualShareLoadBalancer extends LoadBalancer {

	@Override
	public float getInstanceShare(TaskInstance taskInstance) {
		
		if (task == null) {
			throw new RuntimeException("Load Balancer called with null Task");
		}
		
		return 1 / (float)task.getInstances().size();
	}

	public static class Builder implements ObjectBuilder<LoadBalancer> {

		@Override
		public LoadBalancer build() {
			return new EqualShareLoadBalancer();
		}
		
	}


}
