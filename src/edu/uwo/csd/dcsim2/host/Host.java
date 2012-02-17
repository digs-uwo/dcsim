package edu.uwo.csd.dcsim2.host;

import java.util.Vector;

import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.host.resourcemanager.BandwidthManager;
import edu.uwo.csd.dcsim2.host.resourcemanager.CpuManager;
import edu.uwo.csd.dcsim2.host.resourcemanager.MemoryManager;
import edu.uwo.csd.dcsim2.host.resourcemanager.StorageManager;
import edu.uwo.csd.dcsim2.vm.*;

public class Host extends SimulationEntity {

	private Vector<Cpu> cpus;
	private int memory;	
	private int bandwidth;
	private long storage;
	
	private CpuManager cpuManager;
	private MemoryManager memoryManager;
	private BandwidthManager bandwidthManager;
	private StorageManager storageManager;
	private CpuScheduler cpuScheduler;
	
	private Vector<VMAllocation> vmAllocations;
	
	public enum HostState {ON, SUSPENDED, OFF, POWERING_ON, SUSPENDING, POWERING_OFF, FAILED;}
	
	private HostState state;
	
	public Host(int nCpu, int nCores, int coreCapacity, int memory, int bandwidth, long storage,
			CpuManager cpuManager, MemoryManager memoryManager, BandwidthManager bandwidthManager, StorageManager storageManager, CpuScheduler cpuScheduler) {
		
		cpus = new Vector<Cpu>();
		for (int i = 0; i < nCpu; ++i) {
			cpus.add(new Cpu(nCores, coreCapacity));
		}
		
		initializeHost(cpus, memory, bandwidth, storage, cpuManager, memoryManager, bandwidthManager, storageManager, cpuScheduler);
	}
	
	public Host(Vector<Cpu> cpus, int memory, int bandwidth, long storage,
			CpuManager cpuManager, MemoryManager memoryManager, BandwidthManager bandwidthManager, StorageManager storageManager, CpuScheduler cpuScheduler) {
		initializeHost(cpus, memory, bandwidth, storage, cpuManager, memoryManager, bandwidthManager, storageManager, cpuScheduler);		
	}
	
	private void initializeHost(Vector<Cpu> cpus, int memory, int bandwidth, long storage,
			CpuManager cpuManager, MemoryManager memoryManager, BandwidthManager bandwidthManager, StorageManager storageManager, CpuScheduler cpuScheduler) {
		
		this.cpus = cpus;
		this.memory = memory;
		this.bandwidth = bandwidth;
		this.storage = storage;
		
		this.cpuManager = cpuManager;
		cpuManager.setHost(this);
		
		this.memoryManager = memoryManager;
		memoryManager.setHost(this);
		
		this.storageManager = storageManager;
		storageManager.setHost(this);
		
		this.cpuScheduler = cpuScheduler;
		cpuScheduler.setHost(this);
		
		vmAllocations = new Vector<VMAllocation>();
		
		//set default state
		state = HostState.ON;
	}
	
	@Override
	public void handleEvent(Event e) {
		// TODO Auto-generated method stub
		
	}

	
	public boolean isCapable(VMDescription vmDescription) {
		return cpuManager.isCapable(vmDescription) && 
				memoryManager.isCapable(vmDescription) &&	
				bandwidthManager.isCapable(vmDescription) && 
				storageManager.isCapable(vmDescription);
	}
	
	public boolean hasCapacity(VMAllocationRequest vmAllocationRequest) {
		return cpuManager.hasCapacity(vmAllocationRequest) &&
				memoryManager.hasCapacity(vmAllocationRequest) &&
				bandwidthManager.hasCapacity(vmAllocationRequest) &&
				storageManager.hasCapacity(vmAllocationRequest);
	}
	
	public VMAllocation allocate(VMAllocationRequest vmAllocationRequest) {
		VMAllocation vmAllocation = new VMAllocation(vmAllocationRequest.getVMDescription(), this);
		
		//TODO allocate VM
		
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
		} else if (state == HostState.FAILED){
			state = HostState.POWERING_ON;
			//TODO: send message with failed to on delay which when received sets state to ON
		} else {
			//TODO: handle how? report? should not happen
		}
	}
	
	public void fail() {
		state = HostState.FAILED;
	}

	//ACCESSOR METHODS
	
	public Vector<Cpu> getCpus() {
		return cpus;
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
	
	public CpuScheduler getCpuScheduler() {
		return cpuScheduler;
	}
	
	public void setCpuScheduler(CpuScheduler cpuScheduler) {
		this.cpuScheduler = cpuScheduler;
	}
	
	public Vector<VMAllocation> getVMAllocations() {
		return vmAllocations;
	}
}
