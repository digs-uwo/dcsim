package edu.uwo.csd.dcsim2.vm;

import java.util.Vector;

import edu.uwo.csd.dcsim2.core.*;

public class VM extends SimulationEntity {

	private VMDescription vmDescription;
	private Vector<Integer> vCoreInUse;
	private int memoryInUse;
	private int bandwidthInUse;
	private long storageInUse;
	
	public VM(VMDescription vmDescription) {
		this.vmDescription = vmDescription;
	}
	
	public VMDescription getVMDescription() {
		return vmDescription;
	}
	
	public Vector<Integer> getVCoreInUse() {
		return vCoreInUse;
	}
	
	public int getMemoryInUse() {
		return memoryInUse;
	}
	
	public void setMemoryInUse(int memoryInUse) {
		this.memoryInUse = memoryInUse;
	}
	
	public int getBandwidthInUse() {
		return bandwidthInUse;
	}
	
	public void setBandwidthInUse(int bandwidthInUse) {
		this.bandwidthInUse = bandwidthInUse;
	}
	
	public long getStorageInUse() {
		return storageInUse;
	}
	
	public void setStorageInUse(long storageInUse) {
		this.storageInUse = storageInUse;
	}
	
	@Override
	public void handleEvent(Event e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void update() {
		// TODO Auto-generated method stub
		
	}

}
