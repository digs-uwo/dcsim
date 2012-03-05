package edu.uwo.csd.dcsim2.host;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim2.application.*;
import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.host.resourcemanager.*;
import edu.uwo.csd.dcsim2.host.scheduler.CpuScheduler;
import edu.uwo.csd.dcsim2.vm.*;

public class Host extends SimulationEntity {

	private static Logger logger = Logger.getLogger(Host.class);
	
	/**
	 * Event types for events that Host receives
	 */
	public static final int HOST_SUBMIT_VM_EVENT = 1;
	
	public static final int HOST_POWER_ON_EVENT = 2;
	public static final int HOST_POWER_OFF_EVENT = 3;
	public static final int HOST_SUSPEND_EVENT = 4;
	public static final int HOST_COMPLETE_POWER_ON_EVENT = 5;
	public static final int HOST_COMPLETE_SUSPEND_EVENT = 6;
	public static final int HOST_COMPLETE_POWER_OFF_EVENT = 7;
	
	private static int nextId = 1;
	
	private int id;
	private ArrayList<Cpu> cpus;
	private int memory;	
	private int bandwidth;
	private long storage;
	
	private CpuManager cpuManager;
	private MemoryManager memoryManager;
	private BandwidthManager bandwidthManager;
	private StorageManager storageManager;
	private CpuScheduler cpuScheduler;
	
	private ArrayList<VMAllocation> vmAllocations;
	private VMAllocation privDomainAllocation;
	
	public enum HostState {ON, SUSPENDED, OFF, POWERING_ON, SUSPENDING, POWERING_OFF, FAILED;}
	private ArrayList<Event> powerOnEventQueue = new ArrayList<Event>();
	
	private HostState state;
	
	public Host(int nCpu, int nCores, int coreCapacity, int memory, int bandwidth, long storage,
			CpuManager cpuManager, MemoryManager memoryManager, BandwidthManager bandwidthManager, StorageManager storageManager, CpuScheduler cpuScheduler) {
		
		cpus = new ArrayList<Cpu>();
		for (int i = 0; i < nCpu; ++i) {
			cpus.add(new Cpu(nCores, coreCapacity));
		}
		
		initializeHost(cpus, memory, bandwidth, storage, cpuManager, memoryManager, bandwidthManager, storageManager, cpuScheduler);
	}
	
	public Host(ArrayList<Cpu> cpus, int memory, int bandwidth, long storage,
			CpuManager cpuManager, MemoryManager memoryManager, BandwidthManager bandwidthManager, StorageManager storageManager, CpuScheduler cpuScheduler) {
		initializeHost(cpus, memory, bandwidth, storage, cpuManager, memoryManager, bandwidthManager, storageManager, cpuScheduler);		
	}
	
	private void initializeHost(ArrayList<Cpu> cpus, int memory, int bandwidth, long storage,
			CpuManager cpuManager, MemoryManager memoryManager, BandwidthManager bandwidthManager, StorageManager storageManager, CpuScheduler cpuScheduler) {
		
		this.id = nextId++;
		this.cpus = cpus;
		this.memory = memory;
		this.bandwidth = bandwidth;
		this.storage = storage;
		
		setCpuManager(cpuManager);
		setMemoryManager(memoryManager);
		setBandwidthManager(bandwidthManager);
		setStorageManager(storageManager);
		setCpuScheduler(cpuScheduler);
				
		vmAllocations = new ArrayList<VMAllocation>();
		
		/*
		 * Create and allocate privileged domain
		 */
		
		//description allows privileged domain to use any or all of the resources of the host
		VMDescription privDomainDescription = new VMDescription(getCoreCount(), getMaxCoreCapacity(), 0, bandwidth, 0, new VmmApplicationFactory());
		
		//create the allocation
		privDomainAllocation = new VMAllocation(privDomainDescription, this);
		
		//create an initial allocation request TODO should this be in the individual managers?
		VMAllocationRequest privRequest = new VMAllocationRequest(privDomainDescription,
				new CpuAllocation(1, 200), //allocate 200 CPU TODO how should this be determined?
				new MemoryAllocation(0),
				new BandwidthAllocation(0), //TODO how should this be handled? do we increase the bandwidth allocation when there is a migration only?
				new StorageAllocation(0));
		
		//request allocations from resource managers
		cpuManager.allocatePrivDomain(privRequest, privDomainAllocation);
		memoryManager.allocatePrivDomain(privRequest, privDomainAllocation);
		bandwidthManager.allocatePrivDomain(privRequest, privDomainAllocation);
		storageManager.allocatePrivDomain(privRequest, privDomainAllocation);
		
		privDomainAllocation.attachVm(privDomainDescription.createVM());
		
		
		
		//set default state
		state = HostState.ON;
	}
	
	@Override
	public void handleEvent(Event e) {
		
		/**if the Host is in the process of powering on, queue any received events. This effectively
		 * simulates the event sender retrying until the host has powered on, in a simplified fashion.
		 */
		if (state == Host.HostState.POWERING_ON) {
			powerOnEventQueue.add(e);
			return;
		}
		
		switch (e.getType()) {
			case Host.HOST_SUBMIT_VM_EVENT:
				VMAllocationRequest vmAllocationRequest = (VMAllocationRequest)e.getData().get("vmAllocationRequest");
				submitVM(vmAllocationRequest);
				break;
			case Host.HOST_POWER_ON_EVENT:
				powerOn();
				break;
			case Host.HOST_COMPLETE_POWER_ON_EVENT:
				completePowerOn();
				break;
			case Host.HOST_POWER_OFF_EVENT:
				powerOff();
				break;
			case Host.HOST_COMPLETE_POWER_OFF_EVENT:
				completePowerOff();
				break;
			case Host.HOST_SUSPEND_EVENT:
				suspend();
				break;
			case Host.HOST_COMPLETE_SUSPEND_EVENT:
				completeSuspend();
				break;
			default:
				//TODO throw exception
				break;
		}
	}
	
	public void submitVM(VMAllocationRequest vmAllocationRequest) {
		
		//create new allocation & allocate it resources
		VMAllocation newAllocation = allocate(vmAllocationRequest);
		
		//add the allocation to the Host list of allocations
		vmAllocations.add(newAllocation);
		
		//create a new VM in the allocation
		VM newVm = newAllocation.getVMDescription().createVM();
		newAllocation.setVm(newVm);
		newVm.setVMAllocation(newAllocation);
		
		logger.info("Host #" + this.getId() + " allocated & created VM #" + newAllocation.getVm().getId());
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
		
		//allocate CPU
		cpuManager.allocateResource(vmAllocationRequest, vmAllocation);
		
		//allocate memory
		memoryManager.allocateResource(vmAllocationRequest, vmAllocation);
		
		//allocate bandwidth
		bandwidthManager.allocateResource(vmAllocationRequest, vmAllocation);
		
		//allocate storage
		storageManager.allocateResource(vmAllocationRequest, vmAllocation);
		
		return vmAllocation;
	}
	
	/*
	 * HOST STATE OPERATIONS
	 */
	
	public void suspend() {
		if (state != HostState.SUSPENDED && state != HostState.SUSPENDING) {
			state = HostState.SUSPENDING;
			long delay = Long.parseLong(Simulation.getSimulation().getProperty("hostSuspendDelay"));
			Simulation.getSimulation().sendEvent(
					new Event(Host.HOST_COMPLETE_SUSPEND_EVENT,
							Simulation.getSimulation().getSimulationTime() + delay,
							this, this));
		}
	}
	
	public void powerOff() {
		if (state != HostState.OFF && state != HostState.POWERING_OFF) {
			state = HostState.POWERING_OFF;
			long delay = Long.parseLong(Simulation.getSimulation().getProperty("hostPowerOffDelay"));
			Simulation.getSimulation().sendEvent(
					new Event(Host.HOST_COMPLETE_POWER_OFF_EVENT,
							Simulation.getSimulation().getSimulationTime() + delay,
							this, this));
		}
	}
	
	public void powerOn() {
		if (state != HostState.ON && state != HostState.POWERING_ON) {
			state = HostState.POWERING_ON;
			long delay = 0;
			switch (state) {
				case SUSPENDED:
					delay = Long.parseLong(Simulation.getSimulation().getProperty("hostPowerOnFromSuspendDelay"));
					break;
				case OFF:
					delay = Long.parseLong(Simulation.getSimulation().getProperty("hostPowerOnFromOffDelay"));
					break;
				case FAILED:					
					delay = Long.parseLong(Simulation.getSimulation().getProperty("hostPowerOnFromFailedDelay"));
					break;
				case POWERING_OFF:
					delay = Long.parseLong(Simulation.getSimulation().getProperty("hostPowerOffOffDelay"));
					delay += Long.parseLong(Simulation.getSimulation().getProperty("hostPowerOnFromOffDelay"));
					break;
				case SUSPENDING:
					delay = Long.parseLong(Simulation.getSimulation().getProperty("hostSuspendDelay"));
					delay += Long.parseLong(Simulation.getSimulation().getProperty("hostPowerOnFromSuspendDelay"));
					break;
			}

			Simulation.getSimulation().sendEvent(
				new Event(Host.HOST_COMPLETE_POWER_ON_EVENT,
						Simulation.getSimulation().getSimulationTime() + delay,
						this, this));
		}
	}
	
	private void completePowerOn() {
		if (state != HostState.ON) {
			state = HostState.ON;
			for (Event e : powerOnEventQueue) {
				handleEvent(e);
			}
		}
	}
	
	private void completePowerOff() {
		state = HostState.OFF;
	}
	
	private void completeSuspend() {
		state = HostState.SUSPENDED;
	}
	
	public void fail() {
		state = HostState.FAILED;
	}

	//ACCESSOR & MUTATOR METHODS
	
	public int getId() {
		return id;
	}
	
	public ArrayList<Cpu> getCpus() {
		return cpus;
	}
	
	public int getMaxCoreCapacity() {
		int max = 0;
		for (Cpu cpu : cpus) {
			if (cpu.getCoreCapacity() > max) {
				max = cpu.getCoreCapacity();
			}
		}
		return max;
	}
	
	public int getCoreCount() {
		int cores = 0;
		for (Cpu cpu : cpus) {
			cores += cpu.getCores();
		}
		return cores;
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
		cpuManager.setHost(this);
	}
	
	public MemoryManager getMemoryManager() {
		return memoryManager;
	}
	
	public void setMemoryManager(MemoryManager memoryManager) {
		this.memoryManager = memoryManager;
		memoryManager.setHost(this);
	}
	
	public BandwidthManager getBandwidthManager() {
		return bandwidthManager;
	}
	
	public void setBandwidthManager(BandwidthManager bandwidthManager) {
		this.bandwidthManager = bandwidthManager;
		bandwidthManager.setHost(this);
	}
	
	public StorageManager getStorageManager() {
		return storageManager;
	}
	
	public void setStorageManager(StorageManager storageManager) {
		this.storageManager = storageManager;
		storageManager.setHost(this);
	}
	
	public CpuScheduler getCpuScheduler() {
		return cpuScheduler;
	}
	
	public void setCpuScheduler(CpuScheduler cpuScheduler) {
		this.cpuScheduler = cpuScheduler;
		cpuScheduler.setHost(this);
	}
	
	public ArrayList<VMAllocation> getVMAllocations() {
		return vmAllocations;
	}
	
	public VMAllocation getPrivDomainAllocation() {
		return privDomainAllocation;
	}
}
