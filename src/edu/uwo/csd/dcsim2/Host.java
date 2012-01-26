package edu.uwo.csd.dcsim2;

import resmanager.CpuManager;
import edu.uwo.csd.dcsim2.core.*;

public class Host extends SimulationEntity {

	private int cores;
	private int coreCapacity;
	private int memory;	
	private int bandwidth;
	private long storage;
	
	private CpuManager cpuManager;
	
	
	@Override
	public void handleEvent(Event e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void update() {
		// TODO Auto-generated method stub
		
	}
	
	public int getCores() {
		return cores;
	}
	
	public int getCoreCapacity() {
		return coreCapacity;
	}
	
	public int getMemory() {
		return memory;
	}
	
	public int getBandwidth() {
		return bandwidth;
	}
	
	public long getStorage() {
		return storage;
	}
	
	public CpuManager getCpuManager() {
		return cpuManager;
	}

}
