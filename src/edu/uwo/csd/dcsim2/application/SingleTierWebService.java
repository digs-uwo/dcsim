package edu.uwo.csd.dcsim2.application;

import edu.uwo.csd.dcsim2.application.loadbalancer.EqualShareLoadBalancer;
import edu.uwo.csd.dcsim2.application.workload.*;
import edu.uwo.csd.dcsim2.vm.*;

public class SingleTierWebService extends Service {

	public SingleTierWebService(Workload workload, int cores, int coreCapacity, int memory, int bandwidth, long storage, double cpuPerWork, double bwPerWork, double cpuOverhead) {
		this(workload, cores, coreCapacity, memory, bandwidth, storage, cpuPerWork, bwPerWork, cpuOverhead, 1);
	}
	
	public SingleTierWebService(Workload workload, int cores, int coreCapacity, int memory, int bandwidth, long storage, double cpuPerWork, double bwPerWork, double cpuOverhead, int tierMin) {
		setWorkload(workload);
		
		WebServerTier webServerTier = new WebServerTier(memory, storage, cpuPerWork, bwPerWork, cpuOverhead);
		webServerTier.setLoadBalancer(new EqualShareLoadBalancer());
		webServerTier.setWorkTarget(workload);
		
		workload.setWorkTarget(webServerTier);
		
		VMDescription vmDescription = new VMDescription(cores, coreCapacity, memory, bandwidth, storage, webServerTier);
		
		ServiceTier webTier = new ServiceTier(webServerTier, vmDescription, tierMin);
		
		getServiceTiers().add(webTier);
	}
	
}
