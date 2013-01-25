package edu.uwo.csd.dcsim.application;

import edu.uwo.csd.dcsim.application.loadbalancer.EqualShareLoadBalancer;
import edu.uwo.csd.dcsim.application.workload.Workload;
import edu.uwo.csd.dcsim.vm.VMDescription;

/**
 * Provides factory methods to construct common Service configurations. Additional factory methods will be added
 * in future releases.
 * 
 * @author Michael Tighe
 *
 */
public final class Services {

	private Services() {}
	
	/**
	 * Creates a new single tiered Service
	 * @param workload
	 * @param cores
	 * @param coreCapacity
	 * @param memory
	 * @param bandwidth
	 * @param storage
	 * @param cpuPerWork
	 * @param bwPerWork
	 * @param cpuOverhead
	 * @param tierMin
	 * @param tierMax
	 * @return
	 */
	public static Service singleTierInteractiveService(Workload workload, 
			int cores, int coreCapacity, int memory, int bandwidth, long storage, 
			double cpuPerWork, double cpuOverhead, 
			int tierMin, int tierMax) {
		
		InteractiveApplicationTier appTier = new InteractiveApplicationTier(memory, bandwidth, storage, cpuPerWork, cpuOverhead);
		appTier.setLoadBalancer(new EqualShareLoadBalancer());
		VMDescription vmDescription = new VMDescription(cores, coreCapacity, memory, bandwidth, storage, appTier);
		
		ServiceBuilder builder = new ServiceBuilder().workload(workload)
				.tier(appTier, vmDescription, tierMin, tierMax);
		
		return builder.build();
		
	}
	
}
