package edu.uwo.csd.dcsim.host;

import java.util.ArrayList;
import java.util.HashSet;

import edu.uwo.csd.dcsim.application.*;
import edu.uwo.csd.dcsim.common.ObjectBuilder;
import edu.uwo.csd.dcsim.common.ObjectFactory;
import edu.uwo.csd.dcsim.common.Utility;
import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.core.metrics.*;
import edu.uwo.csd.dcsim.host.power.*;
import edu.uwo.csd.dcsim.host.resourcemanager.*;
import edu.uwo.csd.dcsim.host.scheduler.CpuScheduler;
import edu.uwo.csd.dcsim.vm.*;

/**
 * A Host machine (server) within a DataCentre. Hosts VMs (Virtual Machines).
 * 
 * @author Michael Tighe
 *
 */
public final class Host implements SimulationEventListener {
	
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
	
	public static final int HOST_MIGRATE_EVENT = 8;
	public static final int HOST_MIGRATE_COMPLETE_EVENT = 9;
	
	public static final String AVERAGE_ACTIVE_METRIC = "avgActiveHosts";
	public static final String MIN_ACTIVE_METRIC = "minActiveHosts";
	public static final String MAX_ACTIVE_METRIC = "maxActiveHosts";
	public static final String POWER_CONSUMED_METRIC = "powerConsumed";
	public static final String AVERAGE_UTILIZATION_METRIC = "avgHostUtil";
	public static final String HOST_TIME_METRIC = "hostTime";
	
	private Simulation simulation;
	
	private int id;
	private int nCpu;
	private int nCores;
	private int coreCapacity;
	private int memory;	//in MB
	private int bandwidth; //in KB
	private long storage; //in MB
	
	private CpuManager cpuManager;
	private MemoryManager memoryManager;
	private BandwidthManager bandwidthManager;
	private StorageManager storageManager;
	private CpuScheduler cpuScheduler;
	private HostPowerModel powerModel;
	
	private ArrayList<VMAllocation> vmAllocations = new ArrayList<VMAllocation>();
	private VMAllocation privDomainAllocation;
	private ArrayList<VMAllocation> migratingIn = new ArrayList<VMAllocation>();
	private ArrayList<VMAllocation> migratingOut = new ArrayList<VMAllocation>();
	private HashSet<VMAllocation> pendingOutgoingMigrations = new HashSet<VMAllocation>();
	
	public enum HostState {ON, SUSPENDED, OFF, POWERING_ON, SUSPENDING, POWERING_OFF, FAILED;}
	private ArrayList<Event> powerOnEventQueue = new ArrayList<Event>();
	private boolean powerOffAfterMigrations = false;
	
	/*
	 * Simulation metrics
	 */
	private long timeActive = 0; //time this host has spent active (ON)
	private double utilizationSum = 0; //used to calculate average utilization
	private double powerConsumed = 0; //total power consumed by the host
	
	private HostState state;
	
	private Host(Builder builder) {
		
		this.simulation = builder.simulation;
		
		this.id = simulation.nextId(Host.class.toString());
		
		this.nCpu = builder.nCpu;
		this.nCores = builder.nCores;
		this.coreCapacity = builder.coreCapacity;

		
		this.memory = builder.memory;
		this.bandwidth = builder.bandwidth;
		this.storage = builder.storage;
		
		setCpuManager(builder.cpuManagerFactory.newInstance());
		setMemoryManager(builder.memoryManagerFactory.newInstance());
		setBandwidthManager(builder.bandwidthManagerFactory.newInstance());
		setStorageManager(builder.storageManagerFactory.newInstance());
		setCpuScheduler(builder.cpuSchedulerFactory.newInstance());
		setHostPowerModel(builder.powerModel);
					
		/*
		 * Create and allocate privileged domain
		 */
		
		//description allows privileged domain to use any or all of the resources of the host
		VMDescription privDomainDescription = new VMDescription(getCoreCount(), getCoreCapacity(), builder.privMemory, builder.privBandwidth, builder.privStorage, builder.vmmApplicationFactory);
		
		//create the allocation
		privDomainAllocation = new VMAllocation(privDomainDescription, this);
		
		//request allocations from resource managers. Each manager determines how much resource to allocate
		cpuManager.allocatePrivDomain(privDomainAllocation, builder.privCpu);
		memoryManager.allocatePrivDomain(privDomainAllocation, builder.privMemory);
		bandwidthManager.allocatePrivDomain(privDomainAllocation, builder.privBandwidth);
		storageManager.allocatePrivDomain(privDomainAllocation, builder.privStorage);

		PrivDomainVM privVM = new PrivDomainVM(simulation, privDomainDescription, privDomainDescription.getApplicationFactory().createApplication(simulation));
		privDomainAllocation.attachVm(privVM);

		//set default state
		state = HostState.ON;
		
		//initialize metric output formatting
		AggregateMetric.getSimulationMetric(simulation, POWER_CONSUMED_METRIC).initializeOutputFormatter(new PowerFormatter());
		AggregateMetric.getSimulationMetric(simulation, HOST_TIME_METRIC).initializeOutputFormatter(new TimeFormatter(TimeFormatter.TimeUnit.SECONDS, TimeFormatter.TimeUnit.HOURS));
		WeightedAverageMetric.getSimulationMetric(simulation, AVERAGE_UTILIZATION_METRIC).initializeOutputFormatter(new PercentageFormatter());
	}
	
	/**
	 * Builds a new Host object. This is the only way to instantiate Host.
	 * 
	 * @author Michael Tighe
	 *
	 */
	public static class Builder implements ObjectBuilder<Host> {

		private final Simulation simulation;
		
		private int nCpu = -1;
		private int nCores = -1;
		private int coreCapacity = -1;
		private int memory = -1;
		private int bandwidth = -1;
		private long storage = -1;
		private int privCpu = 0;
		private int privMemory = 0;
		private int privBandwidth = 0;
		private long privStorage = 0;
		
		private ObjectFactory<? extends CpuManager> cpuManagerFactory = null;
		private ObjectFactory<? extends MemoryManager> memoryManagerFactory = null;
		private ObjectFactory<? extends BandwidthManager> bandwidthManagerFactory = null;
		private ObjectFactory<? extends StorageManager> storageManagerFactory = null;
		
		private ObjectFactory<? extends CpuScheduler> cpuSchedulerFactory = null; 
		
		private HostPowerModel powerModel = null;
		
		private ApplicationFactory vmmApplicationFactory = new VmmApplicationFactory(); //default value
		
		public Builder(Simulation simulation) {
			if (simulation == null)
				throw new NullPointerException();
			this.simulation = simulation;
		}
		
		public Builder nCpu(int val) {this.nCpu = val; return this;}
		
		public Builder nCores(int val) {this.nCores = val; return this;}
		
		public Builder coreCapacity(int val) {this.coreCapacity = val; return this;}
		
		public Builder memory(int val) {this.memory = val; return this;}
		
		public Builder bandwidth(int val) {this.bandwidth = val; return this;}
		
		public Builder storage(long val) {this.storage = val; return this;}
				
		public Builder privCpu(int val) {this.privCpu = val; return this;}
		
		public Builder privMemory(int val) {this.privMemory = val; return this;}
		
		public Builder privBandwidth(int val) {this.privBandwidth = val; return this;}
		
		public Builder privStorage(long val) {this.privStorage = val; return this;}
		
		public Builder cpuManagerFactory(ObjectFactory<? extends CpuManager> cpuManagerFactory) {
			this.cpuManagerFactory = cpuManagerFactory;
			return this;
		}
		
		public Builder memoryManagerFactory(ObjectFactory<? extends MemoryManager> memoryManagerFactory) {
			this.memoryManagerFactory = memoryManagerFactory;
			return this;
		}
		
		public Builder bandwidthManagerFactory(ObjectFactory<? extends BandwidthManager> bandwidthManagerFactory) {
			this.bandwidthManagerFactory = bandwidthManagerFactory;
			return this;
		}
		
		public Builder storageManagerFactory(ObjectFactory<? extends StorageManager> storageManagerFactory) {
			this.storageManagerFactory = storageManagerFactory;
			return this;
		}
		
		public Builder cpuSchedulerFactory(ObjectFactory<? extends CpuScheduler> cpuSchedulerFactory) {
			this.cpuSchedulerFactory = cpuSchedulerFactory;
			return this;
		}
		
		public Builder powerModel(HostPowerModel powerModel) {
			this.powerModel = powerModel;
			return this;
		}
		
		public Builder vmmApplicationFactory(ApplicationFactory vmmApplicationFactory) {
			this.vmmApplicationFactory = vmmApplicationFactory;
			return this;
		}
		
		@Override
		public Host build() {
			
			if (nCpu == -1 || nCores == -1 || coreCapacity == -1 || memory == -1 || bandwidth == -1 || storage == -1)
				throw new IllegalStateException("Must specific Host resources before building Host");
			if (cpuManagerFactory == null)
				throw new IllegalStateException("Must specify CPU Manager factory before building Host");
			if (bandwidthManagerFactory == null)
				throw new IllegalStateException("Must specify Bandwidth Manager factory before building Host");
			if (memoryManagerFactory == null)
				throw new IllegalStateException("Must specify Memory Manager factory before building Host");
			if (storageManagerFactory == null)
				throw new IllegalStateException("Must specify Storage Manager factory before building Host");
			if (cpuSchedulerFactory == null)
				throw new IllegalStateException("Must specify CPU Scheduler factory before building Host");
			if (powerModel == null)
				throw new IllegalStateException("Must specify power model before building Host");
			
			return new Host(this);
		}
		
	}
	
	@Override
	public void handleEvent(Event e) {
		
		/**if the Host is in the process of powering on, queue any received events. This effectively
		 * simulates the event sender retrying until the host has powered on, in a simplified fashion.
		 */
		if (state == Host.HostState.POWERING_ON && e.getType() != HOST_COMPLETE_POWER_ON_EVENT) {
			powerOnEventQueue.add(e);
			
			if (e.getType() == Host.HOST_MIGRATE_EVENT) {
				Host source = (Host)e.getData().get("source");
				VM vm = (VM)e.getData().get("vm");
				source.markVmForMigration(vm);
			}
			
			return;
		}
		
		VMAllocationRequest vmAllocationRequest;
		VMAllocation vmAllocation;
		VM vm;
		Host source;
		
		switch (e.getType()) {
			case Host.HOST_SUBMIT_VM_EVENT:
				vmAllocationRequest = (VMAllocationRequest)e.getData().get("vmAllocationRequest");
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
			case Host.HOST_MIGRATE_EVENT:
				vmAllocationRequest = (VMAllocationRequest)e.getData().get("vmAllocationRequest");
				vm = (VM)e.getData().get("vm");
				source = (Host)e.getData().get("source");
				this.migrateIn(vmAllocationRequest, vm, source);
				break;
			case Host.HOST_MIGRATE_COMPLETE_EVENT:
				vmAllocation = (VMAllocation)e.getData().get("vmAllocation");
				vm = (VM)e.getData().get("vm");
				source = (Host)e.getData().get("source");
				this.completeMigrationIn(vmAllocation, vm, source);
				break;
			default:
				throw new RuntimeException("Host #" + getId() + " received unknown event type "+ e.getType());
		}
	}
	
	/*
	 * Host info
	 */
	
	public double getCurrentPowerConsumption() {
		return powerModel.getPowerConsumption(state, getCpuManager().getCpuUtilization());
	}
	

	
	/*
	 * VM Allocation
	 */
	
	public void submitVM(VMAllocationRequest vmAllocationRequest) {
		
		VMAllocation newAllocation;
		
		//create new allocation & allocate it resources
		try {
			newAllocation = allocate(vmAllocationRequest);
		} catch (AllocationFailedException e) {
			throw new RuntimeException("Could not allocate submitted VM", e);
		}
		
		//add the allocation to the Host list of allocations
		vmAllocations.add(newAllocation);
		
		//create a new VM in the allocation
		VM newVm = newAllocation.getVMDescription().createVM(simulation);
		newAllocation.setVm(newVm);
		newVm.setVMAllocation(newAllocation);
		
		simulation.getLogger().debug("Host #" + this.getId() + " allocated & created VM #" + newAllocation.getVm().getId());
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
	
	public boolean hasCapacity(ArrayList<VMAllocationRequest> vmAllocationRequests) {
		return cpuManager.hasCapacity(vmAllocationRequests) &&
				memoryManager.hasCapacity(vmAllocationRequests) &&
				bandwidthManager.hasCapacity(vmAllocationRequests) &&
				storageManager.hasCapacity(vmAllocationRequests);
	}
	
	public VMAllocation allocate(VMAllocationRequest vmAllocationRequest) throws AllocationFailedException {
		VMAllocation vmAllocation = new VMAllocation(vmAllocationRequest.getVMDescription(), this);
		
		//allocate CPU
		if (!cpuManager.allocateResource(vmAllocationRequest, vmAllocation))
			throw new AllocationFailedException("Allocation on host #" + getId() + " failed on CPU");
		
		//allocate memory
		if (!memoryManager.allocateResource(vmAllocationRequest, vmAllocation))
			throw new AllocationFailedException("Allocation on host #" + getId() + " failed on memory");
		
		//allocate bandwidth
		if (!bandwidthManager.allocateResource(vmAllocationRequest, vmAllocation))
			throw new AllocationFailedException("Allocation on host #" + getId() + " failed on bandwidth");
		
		//allocate storage
		if (!storageManager.allocateResource(vmAllocationRequest, vmAllocation))
			throw new AllocationFailedException("Allocation on host #" + getId() + " failed on storage");
		
		return vmAllocation;
	}
	
	public void deallocate(VMAllocation vmAllocation) {
		cpuManager.deallocateResource(vmAllocation);
		memoryManager.deallocateResource(vmAllocation);
		bandwidthManager.deallocateResource(vmAllocation);
		storageManager.deallocateResource(vmAllocation);
		
		vmAllocations.remove(vmAllocation);
	}
	
	/*
	 * MIGRATION
	 */
	
	/**
	 * A helper function which creates a migration event and send it to this host. To be called by another host or management entity
	 * that wishes to migrate a VM to this host.
	 * @param vmAllocationRequest
	 * @param vm
	 * @param source The host running the VM to be migrated. Note that this may be different than the Event source, since a third entity may trigger the migration.
	 */
	public void sendMigrationEvent(VMAllocationRequest vmAllocationRequest, VM vm, Host source) {
		Event e = new Event(Host.HOST_MIGRATE_EVENT, 
				simulation.getSimulationTime(),
				source, 
				this);
		e.getData().put("vmAllocationRequest", vmAllocationRequest);
		e.getData().put("vm", vm);
		e.getData().put("source", source);
		simulation.sendEvent(e);
	}
	
	private void markVmForMigration(VM vm) {
		if (!vmAllocations.contains(vm.getVMAllocation()))
				throw new IllegalStateException("Attempted to mark VM #" + vm.getId() +" for migration from Host #" + getId() + 
						" but it resides on Host #" + vm.getVMAllocation().getHost().getId());
		
		pendingOutgoingMigrations.add(vm.getVMAllocation());
	}
	
	public boolean isMigrating(VM vm) {
		return migratingOut.contains(vm.getVMAllocation());
	}
	
	public boolean isPendingMigration(VM vm) {
		return pendingOutgoingMigrations.contains(vm.getVMAllocation());
	}
	
	/**
	 * Triggered when a migration event is received.
	 * @param vmAllocationRequest
	 * @param vm
	 * @param source
	 */
	private void migrateIn(VMAllocationRequest vmAllocationRequest, VM vm, Host source) {
		
		//verify source
		if (vm.getVMAllocation().getHost() != source)
			throw new IllegalStateException("Migration failed: Source (host #" + source.getId() + ") does not match VM (#" + 
					vm.getId() + ") location (host #" + 
					vm.getVMAllocation().getHost().getId() + ").");
		
		//create new allocation & allocate it resources
		
		VMAllocation newAllocation;
		try {
			newAllocation = allocate(vmAllocationRequest);
		} catch (AllocationFailedException e) {
			throw new RuntimeException("Allocation failed on Host # " + this.getId() + 
					" for migrating in VM #" + vm.getId(), e);
		}
		
		//add the allocation to the Host list of allocations
		vmAllocations.add(newAllocation);
		
		//add the allocation to migratingIn list
		migratingIn.add(newAllocation);
		
		//add to VMM
		VmmApplication vmm = (VmmApplication)privDomainAllocation.getVm().getApplication();
		vmm.addMigratingVm(vm);	
		
		//inform the source host that the VM is migrating out
		source.migrateOut(vm);
		
		simulation.getLogger().debug("Host #" + this.getId() + " allocated for incoming VM #" + vm.getId());
		
		if (privDomainAllocation.getBandwidth() == 0)
			throw new RuntimeException("Privileged Domain has no bandwidth available for migration");
		
		//for now, assume 1/4 of bandwidth available to VMM is used for each migration... TODO calculate this properly!
		long timeToMigrate = (long)Math.ceil((((double)vm.getResourcesInUse().getMemory() * 1024) / ((double)privDomainAllocation.getBandwidth() / 4)) * 1000);

		//send migration completion message
		Event e = new Event(Host.HOST_MIGRATE_COMPLETE_EVENT,
				simulation.getSimulationTime() + timeToMigrate,
				this, this);
		e.getData().put("vmAllocation", newAllocation);
		e.getData().put("vm", vm);
		e.getData().put("source", source);
		simulation.sendEvent(e);
	}
	
	private void migrateOut(VM vm) {
		//get the allocation for this vm
		VMAllocation vmAllocation = vm.getVMAllocation();
		
		if (migratingOut.contains(vmAllocation))
			throw new IllegalStateException("Migrate out failed: VM #" + vm.getId() + " is already migrating out of Host #" + getId() + ".");
		
		migratingOut.add(vmAllocation);
		
		if (isPendingMigration(vm))
			pendingOutgoingMigrations.remove(vm);
		
		//add to VMM
		VmmApplication vmm = (VmmApplication)privDomainAllocation.getVm().getApplication();
		vmm.addMigratingVm(vm);
		
		simulation.getLogger().debug("Host #" + this.getId() + " migrating out VM #" + vm.getId());
	}
	
	/**
	 * Triggers when a migration complete event is received, to complete a migration.
	 * @param vmAllocation
	 * @param vm
	 * @param source
	 */
	private void completeMigrationIn(VMAllocation vmAllocation, VM vm, Host source) {
		
		//first, inform the source host the the VM has completed migrating out
		source.completeMigrationOut(vm);
		
		//remove from migratingIn list
		migratingIn.remove(vmAllocation);
		
		//remove from VMM
		VmmApplication vmm = (VmmApplication)privDomainAllocation.getVm().getApplication();
		vmm.removeMigratingVm(vm);
		
		//attach VM to allocation
		vmAllocation.setVm(vm);
		vm.setVMAllocation(vmAllocation);
		
		simulation.getLogger().debug("Host #" + this.getId() + " completed migrating incoming VM #" + vm.getId());
		
	}
	
	private void completeMigrationOut(VM vm) {
		//get the allocation for this vm
		VMAllocation vmAllocation = vm.getVMAllocation();
		migratingOut.remove(vmAllocation);
		
		//add to VMM
		VmmApplication vmm = (VmmApplication)privDomainAllocation.getVm().getApplication();
		vmm.removeMigratingVm(vm);
		
		//deallocate the VM
		deallocate(vmAllocation);
				
		simulation.getLogger().debug("Host #" + this.getId() + " deallocated migrating out VM #" + vm.getId());
		
		if (powerOffAfterMigrations && migratingOut.isEmpty() && pendingOutgoingMigrations.isEmpty())
			powerOff();

	}
	
	/*
	 * HOST STATE OPERATIONS
	 */
	
	public void suspend() {
		if (state != HostState.SUSPENDED && state != HostState.SUSPENDING) {
			state = HostState.SUSPENDING;
			long delay = Long.parseLong(Simulation.getProperty("hostSuspendDelay"));
			simulation.sendEvent(
					new Event(Host.HOST_COMPLETE_SUSPEND_EVENT,
							simulation.getSimulationTime() + delay,
							this, this));
		}
	}
	
	public void powerOff() {
		if (state != HostState.OFF && state != HostState.POWERING_OFF) {
			
			if (migratingOut.size() != 0) {
				//if migrations are in progress, power off after they are complete
				powerOffAfterMigrations = true;
			} else {
				state = HostState.POWERING_OFF;
				long delay = Long.parseLong(Simulation.getProperty("hostPowerOffDelay"));
				simulation.sendEvent(
						new Event(Host.HOST_COMPLETE_POWER_OFF_EVENT,
								simulation.getSimulationTime() + delay,
								this, this));
				powerOffAfterMigrations = false;
			}
		}
	}
	
	public void powerOn() {
		if (state != HostState.ON && state != HostState.POWERING_ON) {
			
			long delay = 0;
			switch (state) {
				case SUSPENDED:
					delay = Long.parseLong(Simulation.getProperty("hostPowerOnFromSuspendDelay"));
					break;
				case OFF:
					delay = Long.parseLong(Simulation.getProperty("hostPowerOnFromOffDelay"));
					break;
				case FAILED:					
					delay = Long.parseLong(Simulation.getProperty("hostPowerOnFromFailedDelay"));
					break;
				case POWERING_OFF:
					delay = Long.parseLong(Simulation.getProperty("hostPowerOffDelay"));
					delay += Long.parseLong(Simulation.getProperty("hostPowerOnFromOffDelay"));
					break;
				case SUSPENDING:
					delay = Long.parseLong(Simulation.getProperty("hostSuspendDelay"));
					delay += Long.parseLong(Simulation.getProperty("hostPowerOnFromSuspendDelay"));
					break;
			}
			
			simulation.sendEvent(
				new Event(Host.HOST_COMPLETE_POWER_ON_EVENT,
						simulation.getSimulationTime() + delay,
						this, this));
			
			state = HostState.POWERING_ON;
		}
	}
	
	private void completePowerOn() {
		
		if (state != HostState.ON) {
			state = HostState.ON;
			for (Event e : powerOnEventQueue) {
				handleEvent(e);
			}
			powerOnEventQueue.clear();
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
	
	//METRICS & LOGGING
	
	/*
	 * Output Host data to the log
	 */
	public void logInfo() {

		if (simulation.getLogger().isDebugEnabled()) {
			if (state == HostState.ON) {
				simulation.getLogger().debug("Host #" + getId() + 
						" CPU[" + (int)Math.round(cpuManager.getCpuInUse()) + "/" + cpuManager.getAllocatedCpu() + "/" + cpuManager.getTotalCpu() + "] " +
						" BW[" + bandwidthManager.getAllocatedBandwidth() + "/" + bandwidthManager.getTotalBandwidth() + "] " +
						" MEM[" + memoryManager.getAllocatedMemory() + "/" + memoryManager.getTotalMemory() + "] " +
						" STORAGE[" + storageManager.getAllocatedStorage() + "/" + storageManager.getTotalStorage() + "] " +
						"Power[" + Utility.roundDouble(this.getCurrentPowerConsumption(), 2) + "W]");	
				privDomainAllocation.getVm().logInfo();
			} else {
				simulation.getLogger().debug("Host #" + getId() + " " + state);
			}
			
			for (VMAllocation vmAllocation : vmAllocations) {
				if (vmAllocation.getVm() != null) {
					vmAllocation.getVm().logInfo();
				} else {
					simulation.getLogger().debug("Empty Allocation CPU[" + vmAllocation.getCpu() + "]");
				}
			}
		}
	}
	
	public void updateMetrics() {
		
		if (getCpuManager().getCpuUtilization() > 1)
			throw new IllegalStateException("Host #" + getId() + " reporting CPU utilization of " + (getCpuManager().getCpuUtilization() * 100));
	
		if (getCpuManager().getCpuUtilization() < 0)
			throw new IllegalStateException("Host #" + getId() + " reporting CPU utilization of " + (getCpuManager().getCpuUtilization() * 100));	
		
		
		if (state == HostState.ON) {
			
			AverageMetric.getSimulationMetric(simulation, AVERAGE_ACTIVE_METRIC).incrementCounter();
			MinMetric.getSimulationMetric(simulation, MIN_ACTIVE_METRIC).incrementCounter();
			MaxMetric.getSimulationMetric(simulation, MAX_ACTIVE_METRIC).incrementCounter();
			
			//Collect active host time metric
			timeActive += simulation.getElapsedTime();
			
			AggregateMetric.getSimulationMetric(simulation, HOST_TIME_METRIC).addValue(simulation.getElapsedSeconds());

			//Collect average host utilization metric
			utilizationSum += getCpuManager().getCpuUtilization() * simulation.getElapsedTime();

			WeightedAverageMetric.getSimulationMetric(simulation, AVERAGE_UTILIZATION_METRIC).addValue(getCpuManager().getCpuUtilization(), simulation.getElapsedTime());
			
		}
		
		//Collect power consumed metric
		powerConsumed += getCurrentPowerConsumption() * simulation.getElapsedSeconds();
		
		AggregateMetric.getSimulationMetric(simulation, POWER_CONSUMED_METRIC).addValue(getCurrentPowerConsumption() * simulation.getElapsedSeconds());
		
		for (VMAllocation vmAllocation : vmAllocations) {
			if (vmAllocation.getVm() != null)
				vmAllocation.getVm().updateMetrics();
		}
	}
	
	public static void updateSimulationScopeMetrics(Simulation simulation) {
		
		//Collect Active Host metrics
		AverageMetric.getSimulationMetric(simulation, AVERAGE_ACTIVE_METRIC).addCounterAndReset();
		MinMetric.getSimulationMetric(simulation, MIN_ACTIVE_METRIC).addCounterAndReset();
		MaxMetric.getSimulationMetric(simulation, MAX_ACTIVE_METRIC).addCounterAndReset();

	}
	
	/**
	 * Get the power efficiency of the host.
	 * @param utilization
	 * @return
	 */
	public double getPowerEfficiency(double utilization) {
		return (getTotalCpu() * utilization) / powerModel.getPowerConsumption(utilization);
	}

	//ACCESSOR & MUTATOR METHODS
	
	public int getId() { return id; }
	
	public int getCpuCount() { return nCpu; }
	
	public int getCoreCapacity() { return coreCapacity; }
	
	public int getCoreCount() { return nCores; }
	
	public int getTotalCpu() {	return nCpu * nCores * coreCapacity; }
	
	public int getMemory() { return memory; }
	
	public int getBandwidth() { return bandwidth; }
	
	public long getStorage() {	return storage; }
	
	public HostState getState() { return state; }
	
	public void setState(HostState state) { this.state = state; }
	
	public CpuManager getCpuManager() { 	return cpuManager;	}
	
	public MemoryManager getMemoryManager() { return memoryManager;}
	
	public BandwidthManager getBandwidthManager() { return bandwidthManager; }
	
	public StorageManager getStorageManager() { 	return storageManager; }
	
	public CpuScheduler getCpuScheduler() { 	return cpuScheduler; }
	
	public void setCpuManager(CpuManager cpuManager) {
		this.cpuManager = cpuManager;
		cpuManager.setHost(this);
	}
		
	public void setMemoryManager(MemoryManager memoryManager) {
		this.memoryManager = memoryManager;
		memoryManager.setHost(this);
	}
	
	public void setBandwidthManager(BandwidthManager bandwidthManager) {
		this.bandwidthManager = bandwidthManager;
		bandwidthManager.setHost(this);
	}
	
	public void setStorageManager(StorageManager storageManager) {
		this.storageManager = storageManager;
		storageManager.setHost(this);
	}
	
	public void setCpuScheduler(CpuScheduler cpuScheduler) {
		this.cpuScheduler = cpuScheduler;
		cpuScheduler.setHost(this);
	}
	
	public ArrayList<VMAllocation> getVMAllocations() { return vmAllocations;	}
	
	public VMAllocation getPrivDomainAllocation() { 	return privDomainAllocation; }
	
	public ArrayList<VMAllocation> getMigratingIn() { return migratingIn;	}
	
	public ArrayList<VMAllocation> getMigratingOut() {	return migratingOut; }
	
	public HostPowerModel getPowerModel() { 	return powerModel;	}
	
	public void setHostPowerModel(HostPowerModel powerModel) {
		this.powerModel = powerModel;
	}
	
	public long getTimeActive() { return timeActive; }
	
	public double getPowerConsumed() { return powerConsumed; }
	
	public double getAverageUtilization() { return utilizationSum / timeActive; }

}
