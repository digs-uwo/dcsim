package edu.uwo.csd.dcsim2.application;

import edu.uwo.csd.dcsim2.application.loadbalancer.EqualShareLoadBalancer;
import edu.uwo.csd.dcsim2.application.workload.Workload;
import edu.uwo.csd.dcsim2.vm.VMDescription;

/**
 * Provides factory methods to construct common Service configurations. Additional factory methods will be added
 * in future releases.
 * 
 * @author Michael Tighe
 *
 */
public final class Services {

	private Services() {}
	
	public static Service singleTierInteractiveService(Workload workload, 
			int cores, int coreCapacity, int memory, int bandwidth, long storage, 
			double cpuPerWork, double bwPerWork, double cpuOverhead, 
			int tierMin, int tierMax) {
		
		InteractiveApplicationTier appTier = new InteractiveApplicationTier(memory, storage, cpuPerWork, bwPerWork, cpuOverhead);
		appTier.setLoadBalancer(new EqualShareLoadBalancer());
		VMDescription vmDescription = new VMDescription(cores, coreCapacity, memory, bandwidth, storage, appTier);
		
		ServiceBuilder builder = new ServiceBuilder().workload(workload)
				.tier(appTier, vmDescription, tierMin, tierMax);
		
		return builder.build();
		
	}
	
}
