package edu.uwo.csd.dcsim2.application;

public class StaticApplication extends Application {

	public StaticApplication(int cores, int coreCapacity, int memory, int bandwidth, long storage) {
		for (int i = 0; i < cores; ++i) {
			coreCapacityNeed.add(coreCapacity);
		}
		memoryNeed = memory;
		bandwidthNeed = bandwidth;
		storageNeed = storage;
	}
	
	@Override
	public void updateResourceNeeds() {
		//Nothing to do, resource need is static
	}

}
