package edu.uwo.csd.dcsim2.application;

import java.util.Vector;

public abstract class Application {

	protected Vector<Integer> coreCapacityNeed;
	protected int memoryNeed;
	protected int bandwidthNeed;
	protected long storageNeed;
	
	public Application() {
		coreCapacityNeed = new Vector<Integer>();
	}
	
	public abstract void updateResourceNeeds();
	
	public Vector<Integer> getCoreCapacityNeed() {
		return coreCapacityNeed;
	}
	
	public int getMemoryNeed() {
		return memoryNeed;
	}
	
	public int getBandwidthNeed() {
		return bandwidthNeed;
	}
	
	public long getStorageNeed() {
		return storageNeed;
	}
	
}
