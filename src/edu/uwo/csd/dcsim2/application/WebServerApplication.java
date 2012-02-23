package edu.uwo.csd.dcsim2.application;

import edu.uwo.csd.dcsim2.vm.VirtualResources;

public class WebServerApplication extends Application {

	private static final double CPU_PER_WORK = 1;
	private static final double BW_PER_WORK = 15;
	
	private int memory;
	private long storage;
	
	public WebServerApplication(ApplicationTier applicationTier, int memory, long storage) {
		super(applicationTier);
		this.memory = memory;
		this.storage = storage;
	}

	@Override
	protected VirtualResources calculateRequiredResources(double work) {
		
		VirtualResources requiredResources = new VirtualResources();
		
		double requiredCpu = work * CPU_PER_WORK; //TODO include app idle overhead
		requiredResources.setCpu(requiredCpu);
		
		double requiredBandwidth = work * BW_PER_WORK; 
		requiredResources.setBandwidth(requiredBandwidth);
		
		requiredResources.setMemory(memory);
		requiredResources.setStorage(storage);
		
		return requiredResources;		
	}

	@Override
	protected CompletedWork performWork(VirtualResources resourcesAvailable) {
		
		double cpuWork, bwWork;
		
		/* 
		 * total work completed depends on CPU and BW. Calculate the
		 * amount of work possible for each assuming the other is infinite,
		 * and the minimum of the two is the amount of work completed
		 */
		cpuWork = resourcesAvailable.getCpu() / CPU_PER_WORK;
		bwWork = resourcesAvailable.getBandwidth() / BW_PER_WORK;
		double workCompleted = Math.min(cpuWork, bwWork);
		
		//calculate cpu and bw consumption based on amount of work completed
		double cpuConsumed = workCompleted * CPU_PER_WORK;
		double bandwidthConsumed = workCompleted * BW_PER_WORK;
		
		VirtualResources resourcesConsumed = new VirtualResources();
		resourcesConsumed.setCpu(cpuConsumed);
		resourcesConsumed.setBandwidth(bandwidthConsumed);
		resourcesConsumed.setMemory(resourcesAvailable.getMemory());
		resourcesConsumed.setStorage(resourcesAvailable.getStorage());
		
		return new CompletedWork(workCompleted, resourcesConsumed);
	}



}
