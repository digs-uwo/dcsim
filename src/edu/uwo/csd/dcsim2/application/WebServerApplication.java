package edu.uwo.csd.dcsim2.application;

import edu.uwo.csd.dcsim2.vm.VirtualResources;

public class WebServerApplication extends Application {

	private int memory;
	private long storage;
	
	public WebServerApplication(ApplicationTier applicationTier, int memory, long storage) {
		super(applicationTier);
		this.memory = memory;
		this.storage = storage;
	}

	@Override
	protected VirtualResources calculateRequiredResources(int work) {
		
		VirtualResources requiredResources = new VirtualResources();
		
		int requiredCpu = work; //1 work unit = 1 cpu share, TODO include app idle overhead
		requiredResources.getCores().add(requiredCpu);
		
		int requiredBandwidth = work * 15; //1 work unit = 15 kb bandwidth 
		requiredResources.setBandwidth(requiredBandwidth);
		
		requiredResources.setMemory(memory);
		requiredResources.setStorage(storage);
		
		return requiredResources;		
	}

	@Override
	protected CompletedWork performWork(VirtualResources resourcesAvailable) {
		
		int cpuConsumed = 0;
		int bandwidthConsumed = 0;
		int workCompleted = 0;
		int cpuRemaining = resourcesAvailable.getTotalCpu();
		int bandwidthRemaining = resourcesAvailable.getBandwidth();
		
		while (cpuRemaining > 0 && bandwidthRemaining >= 15) {
			--cpuRemaining;
			bandwidthRemaining -= 15;
			++cpuConsumed;
			bandwidthConsumed += 15;
			++workCompleted;
		}
		
		VirtualResources resourcesConsumed = new VirtualResources();
		resourcesConsumed.getCores().add(cpuConsumed);
		resourcesConsumed.setBandwidth(bandwidthConsumed);
		resourcesConsumed.setMemory(resourcesAvailable.getMemory());
		resourcesConsumed.setStorage(resourcesAvailable.getStorage());
		
		return new CompletedWork(workCompleted, resourcesConsumed);
	}



}
