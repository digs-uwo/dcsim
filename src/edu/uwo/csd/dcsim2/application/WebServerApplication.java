package edu.uwo.csd.dcsim2.application;

import edu.uwo.csd.dcsim2.vm.VirtualResources;

public class WebServerApplication extends InteractiveApplication {

	private double cpuPerWork;
	private double bwPerWork;
	private int memory;
	private long storage;
	
	public WebServerApplication(ApplicationTier applicationTier, int memory, long storage, double cpuPerWork, double bwPerWork, double cpuOverhead) {
		super(applicationTier);
		this.memory = memory;
		this.storage = storage;
		this.cpuPerWork = cpuPerWork;
		this.bwPerWork = bwPerWork;
		
		VirtualResources overhead = new VirtualResources();
		overhead.setCpu(cpuOverhead);
		this.setOverhead(overhead);
	}

	@Override
	protected VirtualResources calculateRequiredResources(double work) {
		
		VirtualResources requiredResources = new VirtualResources();
		
		double requiredCpu = work * cpuPerWork;
		requiredResources.setCpu(requiredCpu);
		
		double requiredBandwidth = work * bwPerWork; 
		requiredResources.setBandwidth(requiredBandwidth);
		
		requiredResources.setMemory(memory);
		requiredResources.setStorage(storage);
		
		return requiredResources;		
	}

	@Override
	protected CompletedWork performWork(VirtualResources resourcesAvailable, double workRemaining) {
		
		double cpuWork, bwWork;

		/* 
		 * total work completed depends on CPU and BW. Calculate the
		 * amount of work possible for each assuming the other is infinite,
		 * and the minimum of the two is the amount of work completed
		 */
		cpuWork = resourcesAvailable.getCpu() / cpuPerWork;
		bwWork = resourcesAvailable.getBandwidth() / bwPerWork;
		double workCompleted = Math.min(cpuWork, bwWork);
		workCompleted = Math.min(workCompleted, workRemaining);
		
		//calculate cpu and bw consumption based on amount of work completed
		double cpuConsumed = workCompleted * cpuPerWork;
		double bandwidthConsumed = workCompleted * bwPerWork;
		
		VirtualResources resourcesConsumed = new VirtualResources();
		resourcesConsumed.setCpu(cpuConsumed);
		resourcesConsumed.setBandwidth(bandwidthConsumed);
		resourcesConsumed.setMemory(resourcesAvailable.getMemory());
		resourcesConsumed.setStorage(resourcesAvailable.getStorage());
		
		return new CompletedWork(workCompleted, resourcesConsumed);
	}


}
