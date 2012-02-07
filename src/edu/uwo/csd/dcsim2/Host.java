package edu.uwo.csd.dcsim2;

import edu.uwo.csd.dcsim2.resmanager.*;
import edu.uwo.csd.dcsim2.core.*;

public class Host extends SimulationEntity {

	private int cores;
	private int coreCapacity;
	private int memory;	
	private int bandwidth;
	private long storage;
	
	private CpuManager cpuManager;
	private MemoryManager memoryManager;
	private BandwidthManager bandwidthManager;
	private StorageManager storageManager;
	
	public enum HostState {ON, SUSPENDED, OFF, POWERING_ON, SUSPENDING, POWERING_OFF;}
	
	private HostState state;
	
	@Override
	public void handleEvent(Event e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void update() {
		// TODO Auto-generated method stub
		
	}
	
	public void suspend() {
		if (state == HostState.ON) {
			state = HostState.SUSPENDING;
			//TODO: send message with suspend delay which when received sets state to SUSPENDED
		} else {
			//TODO: handle how? report? should not happen
		}
	}
	
	public void powerOff() {
		if (state == HostState.ON || state == HostState.SUSPENDED) {
			state = HostState.POWERING_OFF;
			//TODO: send message with power off delay which when received sets state to OFF
		} else {
			//TODO: handle how? report? should not happen
		}
	}
	
	public void powerOn() {
		if (state == HostState.SUSPENDED) {
			state = HostState.POWERING_ON;
			//TODO: send message with suspend to on delay which when received sets state to ON
		} else if (state == HostState.OFF) {
			state = HostState.POWERING_ON;
			//TODO: send message with off to on delay which when received sets state to ON
		} else {
			//TODO: handle how? report? should not happen
		}
	}

	
	//ACCESSOR METHODS
	
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
	
	public HostState getState() {
		return state;
	}
	
	public void setState(HostState state) {
		this.state = state;
	}
	
	public CpuManager getCpuManager() {
		return cpuManager;
	}
	
	public void setCpuManager(CpuManager cpuManager) {
		this.cpuManager = cpuManager;
	}
	
	public MemoryManager getMemoryManager() {
		return memoryManager;
	}
	
	public void setMemoryManager(MemoryManager memoryManager) {
		this.memoryManager = memoryManager;
	}
	
	public BandwidthManager getBandwidthManager() {
		return bandwidthManager;
	}
	
	public void setBandwidthManager(BandwidthManager bandwidthManager) {
		this.bandwidthManager = bandwidthManager;
	}
	
	public StorageManager getStorageManager() {
		return storageManager;
	}
	
	public void setStorageManager(StorageManager storageManager) {
		this.storageManager = storageManager;
	}
}
