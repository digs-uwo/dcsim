package edu.uwo.csd.dcsim2.host;

import java.util.Vector;

import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.host.resourcemanager.BandwidthManager;
import edu.uwo.csd.dcsim2.host.resourcemanager.CpuManager;
import edu.uwo.csd.dcsim2.host.resourcemanager.MemoryManager;
import edu.uwo.csd.dcsim2.host.resourcemanager.StorageManager;
import edu.uwo.csd.dcsim2.vm.*;

public class Host extends UpdatingSimulationEntity {

	private Vector<Processor> processors;
	private int memory;	
	private int bandwidth;
	private long storage;
	
	private CpuManager cpuManager;
	private MemoryManager memoryManager;
	private BandwidthManager bandwidthManager;
	private StorageManager storageManager;
	
	private Vector<VMAllocation> vmAllocations;
	
	public enum HostState {ON, SUSPENDED, OFF, POWERING_ON, SUSPENDING, POWERING_OFF;}
	
	private HostState state;
	
	public Host(int nProcessors, int nCores, int coreCapacity, int memory, int bandwidth, long storage,
			CpuManager cpuManager, MemoryManager memoryManager, BandwidthManager bandwidthManager, StorageManager storageManager) {
		
		processors = new Vector<Processor>();
		for (int i = 0; i < nProcessors; ++i) {
			processors.add(new Processor(nCores, coreCapacity));
		}
		
		initializeHost(processors, memory, bandwidth, storage, cpuManager, memoryManager, bandwidthManager, storageManager);
	}
	
	public Host(Vector<Processor> processors, int memory, int bandwidth, long storage,
			CpuManager cpuManager, MemoryManager memoryManager, BandwidthManager bandwidthManager, StorageManager storageManager) {
		initializeHost(processors, memory, bandwidth, storage, cpuManager, memoryManager, bandwidthManager, storageManager);		
	}
	
	private void initializeHost(Vector<Processor> processors, int memory, int bandwidth, long storage,
			CpuManager cpuManager, MemoryManager memoryManager, BandwidthManager bandwidthManager, StorageManager storageManager) {
		
		this.processors = processors;
		this.memory = memory;
		this.bandwidth = bandwidth;
		this.storage = storage;
		
		this.cpuManager = cpuManager;
		cpuManager.setHost(this);
		
		this.memoryManager = memoryManager;
		memoryManager.setHost(this);
		
		this.storageManager = storageManager;
		storageManager.setHost(this);
		
		vmAllocations = new Vector<VMAllocation>();	
	}
	
	@Override
	public void handleEvent(Event e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void update() {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isCapable(VMDescription vmDescription) {
		return cpuManager.isCapable(vmDescription) && 
				memoryManager.isCapable(vmDescription) &&	
				bandwidthManager.isCapable(vmDescription) && 
				storageManager.isCapable(vmDescription);
	}
	
	public boolean hasCapacity(VMDescription vmDescription) {
		return cpuManager.hasCapacity(vmDescription) &&
				memoryManager.hasCapacity(vmDescription) &&
				bandwidthManager.hasCapacity(vmDescription) &&
				storageManager.hasCapacity(vmDescription);
	}
	
	public VMAllocation allocate(VMDescription vmDescription) {
		VMAllocation vmAllocation = new VMAllocation(vmDescription, this);
		
		if (!cpuManager.allocateResource(vmAllocation)) {
			//TODO exception?
			return null;
		}
		
		if (!memoryManager.allocateResource(vmAllocation)) {
			cpuManager.deallocateResource(vmAllocation);
			//TODO exception?
			return null;
		}
		
		if (!bandwidthManager.allocateResource(vmAllocation)) {
			cpuManager.deallocateResource(vmAllocation);
			memoryManager.deallocateResource(vmAllocation);
			//TODO exception?
			return null;
		}
		
		if (!storageManager.allocateResource(vmAllocation)) {
			cpuManager.deallocateResource(vmAllocation);
			memoryManager.deallocateResource(vmAllocation);
			bandwidthManager.deallocateResource(vmAllocation);
			//TODO exception?
			return null;
		}
		
		vmAllocations.add(vmAllocation);
		return vmAllocation;
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
	
	public Vector<Processor> getProcessors() {
		return processors;
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
	
	public Vector<VMAllocation> vmAllocations() {
		return vmAllocations;
	}
}
