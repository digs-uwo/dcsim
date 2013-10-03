package edu.uwo.csd.dcsim.host;

import java.util.ArrayList;
import java.util.HashSet;

import edu.uwo.csd.dcsim.application.*;
import edu.uwo.csd.dcsim.common.HashCodeUtil;
import edu.uwo.csd.dcsim.common.ObjectBuilder;
import edu.uwo.csd.dcsim.common.ObjectFactory;
import edu.uwo.csd.dcsim.common.Utility;
import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.host.events.*;
import edu.uwo.csd.dcsim.host.events.PowerStateEvent.PowerState;
import edu.uwo.csd.dcsim.host.power.*;
import edu.uwo.csd.dcsim.host.resourcemanager.*;
import edu.uwo.csd.dcsim.host.scheduler.*;
import edu.uwo.csd.dcsim.management.AutonomicManager;
import edu.uwo.csd.dcsim.vm.*;

/**
 * A Host machine (server) within a DataCentre. Hosts VMs (Virtual Machines).
 * 
 * @author Michael Tighe
 *
 */
public final class Host implements SimulationEventListener {
	
	private Simulation simulation;
	
	private int id;
	private int nCpu;
	private int nCores;
	private int coreCapacity;
	private int memory;	//in MB
	private int bandwidth; //in KB
	private int storage; //in MB
	
	private NetworkCard dataNetworkCard;
	private NetworkCard mgmtNetworkCard;
	private Rack rack;
	
	private ResourceManager resourceManager;
	private ResourceScheduler resourceScheduler;
	private HostPowerModel powerModel;
	
	private VmmApplication vmmApplication;
	
	private ArrayList<VmAllocation> vmAllocations = new ArrayList<VmAllocation>();
	private VmAllocation privDomainAllocation;
	private ArrayList<VmAllocation> migratingIn = new ArrayList<VmAllocation>();
	private ArrayList<VmAllocation> migratingOut = new ArrayList<VmAllocation>();
	private HashSet<VmAllocation> pendingOutgoingMigrations = new HashSet<VmAllocation>();
	private ArrayList<VmAllocation> startingVm = new ArrayList<VmAllocation>();
	
	public enum HostState {ON, SUSPENDED, OFF, POWERING_ON, SUSPENDING, POWERING_OFF, FAILED;}
	private ArrayList<Event> powerOnEventQueue = new ArrayList<Event>();
	private PowerStateEvent powerOffAfterMigrations = null;
	
	private AutonomicManager autonomicManager = null;
	
	private final int hashCode;
	
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
		
		this.dataNetworkCard = new NetworkCard(this.bandwidth);
		this.mgmtNetworkCard = new NetworkCard(this.bandwidth);
		
		setResourceManager(builder.resourceManagerFactory.newInstance());
		setHostPowerModel(builder.powerModel);
		setResourceScheduler(builder.resourceSchedulerFactory.newInstance());
		
		resourceScheduler.setHost(this);
		
		/*
		 * Create and allocate privileged domain
		 */
		
		vmmApplication = new VmmApplication(simulation, this, builder.privCpu, builder.privMemory, builder.privBandwidth, builder.privStorage);
		
		VmDescription privDomainDescription = new VmDescription(vmmApplication.getVmmTask());

		//create the allocation
		privDomainAllocation = new VmAllocation(privDomainDescription, this);
		
		//request allocations from resource managers. Each manager determines how much resource to allocate
		resourceManager.allocatePrivDomain(privDomainAllocation, builder.privCpu, builder.privMemory, builder.privBandwidth, builder.privStorage);

		PrivDomainVm privVM = new PrivDomainVm(simulation, privDomainDescription, vmmApplication.getVmmTaskInstance());
		privDomainAllocation.attachVm(privVM);

		//set default state
		//state = HostState.ON;
		state = HostState.OFF;
		
		//write host description to the trace
		//FORMAT: 0,#hd,id,cpuCapacity,memCapacity,bwCapacity,storageCapacity,idlePower,peakPower
		
		simulation.getTraceLogger().info("#hd," + getId() + "," + this.getTotalCpu() + "," +
				this.getMemory() + "," + 
				this.getBandwidth() + "," + 
				this.getStorage() + "," + 
				this.getPowerModel().getPowerConsumption(0) + "," +
				this.getPowerModel().getPowerConsumption(1));
		
		//init hashCode
		hashCode = generateHashCode();
		
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
		private int storage = -1;
		private int privCpu = 0;
		private int privMemory = 0;
		private int privBandwidth = 0;
		private int privStorage = 0;
		
		private ObjectFactory<? extends ResourceManager> resourceManagerFactory = null;
		private ObjectFactory<? extends ResourceScheduler> resourceSchedulerFactory = null;
		
		private HostPowerModel powerModel = null;
				
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
		
		public Builder storage(int val) {this.storage = val; return this;}
				
		public Builder privCpu(int val) {this.privCpu = val; return this;}
		
		public Builder privMemory(int val) {this.privMemory = val; return this;}
		
		public Builder privBandwidth(int val) {this.privBandwidth = val; return this;}
		
		public Builder privStorage(int val) {this.privStorage = val; return this;}
		
		public Builder resourceManagerFactory(ObjectFactory<? extends ResourceManager> resourceManagerFactory) {
			this.resourceManagerFactory = resourceManagerFactory;
			return this;
		}
		
		public Builder resourceSchedulerFactory(ObjectFactory<? extends ResourceScheduler> resourceSchedulerFactory) {
			this.resourceSchedulerFactory = resourceSchedulerFactory;
			return this;
		}
		
		public Builder powerModel(HostPowerModel powerModel) {
			this.powerModel = powerModel;
			return this;
		}
		
		@Override
		public Host build() {
			
			if (nCpu == -1 || nCores == -1 || coreCapacity == -1 || memory == -1 || bandwidth == -1 || storage == -1)
				throw new IllegalStateException("Must specific Host resources before building Host");
			if (resourceManagerFactory == null)
				throw new IllegalStateException("Must specify Resource Manager factory before building Host");
			if (resourceSchedulerFactory == null)
				throw new IllegalStateException("Must specify Resource Scheduler factory before building Host");
			if (powerModel == null)
				throw new IllegalStateException("Must specify power model before building Host");
			
			return new Host(this);
		}
		
	}
	
	public void installAutonomicManager(AutonomicManager manager) {
		autonomicManager = manager;
		manager.setContainer(this);
	}
	
	public void uninstallAutonomicManager() {
		autonomicManager.setContainer(null);
		autonomicManager = null;
	}
	
	public AutonomicManager getAutonomicManager() {
		return autonomicManager;
	}
	
	@Override
	public void handleEvent(Event e) {
		
		/**if the Host is in the process of powering on, queue any received events. This effectively
		 * simulates the event sender retrying until the host has powered on, in a simplified fashion.
		 */

		//determine if we should queue the event (all events except the POWER_ON completion event are queued
		boolean queueEvent = false; //assume no queuing
		if (state == Host.HostState.POWERING_ON) {
			//the host is powering on, so assume queuing
			queueEvent = true;
			
			if (e instanceof PowerStateEvent) {
				PowerStateEvent powerEvent = (PowerStateEvent)e;
				if (powerEvent.getPowerState() == PowerStateEvent.PowerState.POWER_ON &&
						powerEvent.isComplete()) {
					queueEvent = false; //this is the event that will complete the host POWER_ON operation, let it through (do not queue)
				}
			}
		}
		
		//if the event should be queued, do so
		if (queueEvent) {
			powerOnEventQueue.add(e);
			
			//prevent event from triggering post event and callback methods
			e.setBlockPostEvent(true);
			
			//if the queued event is for migration, inform the source of the pending migration
			if (e instanceof MigrateVmEvent) {
				MigrateVmEvent migrateEvent = (MigrateVmEvent)e;			
				migrateEvent.getSource().markVmForMigration(migrateEvent.getVM());
			}
			
			return;
		}

		if (e instanceof PowerStateEvent) {
			//PowerStateEvent, indicating that the host must change power state
			
			PowerStateEvent powerEvent = (PowerStateEvent)e;
			if (powerEvent.getPowerState() == PowerState.POWER_ON) {
				if (powerEvent.isComplete()) {
					completePowerOn();
				} else {
					powerOn(powerEvent);
				}
			} else if (powerEvent.getPowerState() == PowerState.POWER_OFF) {
				if (powerEvent.isComplete()) {
					completePowerOff();
				} else {
					powerOff(powerEvent);
				}
			} else if (powerEvent.getPowerState() == PowerState.SUSPEND) {
				if (powerEvent.isComplete()) {
					completeSuspend();
				} else {
					suspend(powerEvent);
				}
			}
		} else if (e instanceof MigrateVmEvent) {
			//MigrateVmEvent, triggering a VM migration to this host
			
			MigrateVmEvent migrateEvent = (MigrateVmEvent)e;
			if (migrateEvent.isComplete()) {
				this.completeMigrationIn(migrateEvent);
			} else {
				this.migrateIn(migrateEvent);
			}
		} else if (e instanceof SubmitVmEvent) {
			submitVm((SubmitVmEvent)e);
		} else if (e instanceof VmStartEvent) {
			completeVmStart((VmStartEvent)e);
		} else {
			//unknown event
			throw new RuntimeException("Host #" + getId() + " received unknown event type "+ e.getClass());
		}
	}
	
	/*
	 * Host info
	 */
	
	public double getCurrentPowerConsumption() {
		return powerModel.getPowerConsumption(state, getResourceManager().getCpuUtilization());
	}
	

	
	/*
	 * VM Allocation
	 */
	
	public void submitVm(SubmitVmEvent event) {
		
		VmAllocationRequest vmAllocationRequest = event.getVmAllocationRequest();
		
		VmAllocation newAllocation;
		
		//create new allocation & allocate it resources
		try {
			newAllocation = allocate(vmAllocationRequest);
		} catch (AllocationFailedException e) {
			throw new RuntimeException("Allocation failed on Host #" + this.getId() + 
					" VM submission" , e);
			
		}
		
		//add the allocation to the Host list of allocations
		vmAllocations.add(newAllocation);
		startingVm.add(newAllocation);
		
		long vmStartDelay = Long.parseLong(Simulation.getProperty("vmStartTime"));
		
		VmStartEvent vmStartEvent = new VmStartEvent(this, newAllocation);
		event.addEventInSequence(vmStartEvent); //defer completion of the original event until the VmStartEvent is complete
		simulation.sendEvent(vmStartEvent, simulation.getSimulationTime() + vmStartDelay);
		
		simulation.getLogger().debug("Host #" + this.getId() + " created new VM allocation");
	}

	private void completeVmStart(VmStartEvent event) {
				
		VmAllocation newAllocation = event.getVmAllocation();
		
		//create a new VM in the allocation
		Vm newVm = newAllocation.getVMDescription().createVM(simulation);
		newAllocation.setVm(newVm);
		newVm.setVMAllocation(newAllocation);
		startingVm.remove(newAllocation);
		
		simulation.getLogger().debug("Host #" + this.getId() + " created & started VM #" + newAllocation.getVm().getId());
		simulation.getTraceLogger().info("#vs," + newVm.getId() + "," + newVm.getVMAllocation().getHost().getId());
	}
	
	/**
	 * Helper function to facilitate testing by allowing VMs to be directly placed onto specific hosts. Should not be called during normal usage. Does not include VM start delay.
	 * @param vmAllocationRequest
	 */
	public void submitVm(VmAllocationRequest vmAllocationRequest) {
		
		VmAllocation newAllocation;
		
		//create new allocation & allocate it resources
		try {
			newAllocation = allocate(vmAllocationRequest);
		} catch (AllocationFailedException e) {
			throw new RuntimeException("Allocation failed on Host #" + this.getId() + 
					" VM submission" , e);
			
		}
		
		//add the allocation to the Host list of allocations
		vmAllocations.add(newAllocation);
		
		//create a new VM in the allocation
		Vm newVm = newAllocation.getVMDescription().createVM(simulation);
		newAllocation.setVm(newVm);
		newVm.setVMAllocation(newAllocation);
		
		simulation.getLogger().debug(simulation.getSimulationTime() + " Host #" + this.getId() + " allocated & created VM #" + newAllocation.getVm().getId());
		simulation.getTraceLogger().info("#vs," + newVm.getId() + "," + newVm.getVMAllocation().getHost().getId());
	}
	
	public boolean isCapable(VmDescription vmDescription) {
		return resourceManager.isCapable(vmDescription);
	}
	
	public boolean hasCapacity(VmAllocationRequest vmAllocationRequest) {
		return resourceManager.hasCapacity(vmAllocationRequest);
	}
	
	public boolean hasCapacity(ArrayList<VmAllocationRequest> vmAllocationRequests) {
		return resourceManager.hasCapacity(vmAllocationRequests);
	}
	
	public VmAllocation allocate(VmAllocationRequest vmAllocationRequest) throws AllocationFailedException {
		VmAllocation vmAllocation = new VmAllocation(vmAllocationRequest.getVMDescription(), this);
		
		//allocate resources
		if (!resourceManager.allocateResource(vmAllocationRequest, vmAllocation))
			throw new AllocationFailedException("Allocation on host #" + getId() + " failed");
		
		return vmAllocation;
	}
	
	public void deallocate(VmAllocation vmAllocation) {
		resourceManager.deallocateResource(vmAllocation);
		
		vmAllocations.remove(vmAllocation);
	}
	
	/*
	 * MIGRATION
	 */
	
	private void markVmForMigration(Vm vm) {
		if (!vmAllocations.contains(vm.getVMAllocation()))
				throw new IllegalStateException("Attempted to mark VM #" + vm.getId() +" for migration from Host #" + getId() + 
						" but it resides on Host #" + vm.getVMAllocation().getHost().getId());
		
		pendingOutgoingMigrations.add(vm.getVMAllocation());
	}
	
	public boolean isMigrating(Vm vm) {
		return migratingOut.contains(vm.getVMAllocation());
	}
	
	public boolean isPendingMigration(Vm vm) {
		return pendingOutgoingMigrations.contains(vm.getVMAllocation());
	}
	
	/**
	 * Triggered when a migration event is received.
	 * @param vmAllocationRequest
	 * @param vm
	 * @param source
	 */
	private void migrateIn(MigrateVmEvent event) {

		VmAllocationRequest vmAllocationRequest = event.getVMAllocationRequest();
		Vm vm = event.getVM();
		Host source = event.getSource();

		
		//verify source
		if (vm.getVMAllocation().getHost() != source)
			throw new IllegalStateException("Migration failed: Source (host #" + source.getId() + ") does not match VM (#" + 
					vm.getId() + ") location (host #" + 
					vm.getVMAllocation().getHost().getId() + ").");
		
		//create new allocation & allocate it resources
		
		VmAllocation newAllocation;
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
		
		//inform the source host that the VM is migrating out
		source.migrateOut(vm);
		
		simulation.getLogger().debug("Host #" + this.getId() + " allocated for incoming VM #" + vm.getId());
		
		if (privDomainAllocation.getBandwidth() == 0)
			throw new RuntimeException("Privileged Domain has no bandwidth available for migration");
		
		//for now, assume 1/4 of bandwidth available to VMM is used for each migration... TODO calculate this properly!
		long timeToMigrate = (long)Math.ceil((((double)vm.getResourcesScheduled().getMemory() * 1024) / ((double)privDomainAllocation.getBandwidth() / 4)) * 1000);
	
		//send migration completion message
		MigrateVmEvent migCompleteEvent = new MigrateVmEvent(source, this, newAllocation, vm);
		event.addEventInSequence(migCompleteEvent); //defer completion of the original event until the MigrateVmEvent is complete
		simulation.sendEvent(migCompleteEvent, simulation.getSimulationTime() + timeToMigrate);
		
	}
	
	private void migrateOut(Vm vm) {
		//get the allocation for this vm
		VmAllocation vmAllocation = vm.getVMAllocation();
		
		if (migratingOut.contains(vmAllocation)) {
			System.out.println("?");
			throw new IllegalStateException("Migrate out failed: VM #" + vm.getId() + " is already migrating out of Host #" + getId() + ".");
		}
		
		migratingOut.add(vmAllocation);
		
		if (isPendingMigration(vm))
			pendingOutgoingMigrations.remove(vm);
		
		simulation.getLogger().debug("Host #" + this.getId() + " migrating out VM #" + vm.getId());
	}
	
	/**
	 * Triggers when a migration complete event is received, to complete a migration.
	 * @param vmAllocation
	 * @param vm
	 * @param source
	 */
	private void completeMigrationIn(MigrateVmEvent event) {

		VmAllocation vmAllocation = event.getVMAllocation();
		Vm vm = event.getVM();
		Host source = event.getSource();
		
		//first, inform the source host the the VM has completed migrating out
		source.completeMigrationOut(vm);
		
		//remove from migratingIn list
		migratingIn.remove(vmAllocation);
		
		//attach VM to allocation
		vmAllocation.setVm(vm);
		vm.setVMAllocation(vmAllocation);
		
		simulation.getLogger().debug("Host #" + this.getId() + " completed migrating incoming VM #" + vm.getId());
		
	}
	
	private void completeMigrationOut(Vm vm) {
		//get the allocation for this vm
		VmAllocation vmAllocation = vm.getVMAllocation();
		migratingOut.remove(vmAllocation);
		
		//deallocate the VM
		deallocate(vmAllocation);
				
		simulation.getLogger().debug("Host #" + this.getId() + " deallocated migrating out VM #" + vm.getId());
		
		if ((powerOffAfterMigrations != null) && migratingOut.isEmpty() && pendingOutgoingMigrations.isEmpty())
			powerOff(powerOffAfterMigrations);

	}
	
	/*
	 * HOST STATE OPERATIONS
	 */
	
	public void suspend(PowerStateEvent event) {
		if (state != HostState.SUSPENDED && state != HostState.SUSPENDING) {
			state = HostState.SUSPENDING;
			long delay = Long.parseLong(Simulation.getProperty("hostSuspendDelay"));
			
			PowerStateEvent completeEvent = new PowerStateEvent(this, PowerState.SUSPEND, true);
			event.addEventInSequence(completeEvent);
			simulation.sendEvent(completeEvent, simulation.getSimulationTime() + delay);
		}
	}
	
	public void powerOff(PowerStateEvent event) {

		if (state != HostState.OFF && state != HostState.POWERING_OFF) {
			
			if (migratingOut.size() != 0) {
				//if migrations are in progress, power off after they are complete
				powerOffAfterMigrations = event;
			} else {
				state = HostState.POWERING_OFF;
				long delay = Long.parseLong(Simulation.getProperty("hostPowerOffDelay"));
				
				PowerStateEvent completeEvent = new PowerStateEvent(this, PowerState.POWER_OFF, true);
				event.addEventInSequence(completeEvent);
				simulation.sendEvent(completeEvent, simulation.getSimulationTime() + delay);
				
				powerOffAfterMigrations = null;
			}
		}
	}
	
	public void powerOn(PowerStateEvent event) {
		
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
				default:
					throw new RuntimeException("Invalid Host State encountered - " + state);
			}
			
			PowerStateEvent completeEvent = new PowerStateEvent(this, PowerState.POWER_ON, true);
			event.addEventInSequence(completeEvent);
			simulation.sendEvent(completeEvent, simulation.getSimulationTime() + delay);
			
			state = HostState.POWERING_ON;
			
			//inform any managers that the host is turning on
			if (autonomicManager != null)
				autonomicManager.onContainerStart();
		}
	}
	
	private void completePowerOn() {

		if (state != HostState.ON) {
			state = HostState.ON;
			for (Event e : powerOnEventQueue) {
				e.setBlockPostEvent(false);
				handleEvent(e);
				e.triggerPostExecute();
				e.triggerCallback();
			}
			powerOnEventQueue.clear();
		}
	}
	
	private void completePowerOff() {
		state = HostState.OFF;
		
		if (autonomicManager != null)
			autonomicManager.onContainerStop();
	}
	
	private void completeSuspend() {
		state = HostState.SUSPENDED;
		
		if (autonomicManager != null)
			autonomicManager.onContainerStop();
	}
	
	public void fail() {
		state = HostState.FAILED;
	}
	
	/**
	 * If the Host is set to shutdown upon completion its current set of migrations, cancel this shutdown and remain ON.
	 */
	public void cancelPendingShutdown() {
		powerOffAfterMigrations.cancelEventInSequence();
		powerOffAfterMigrations = null;
	}
	
	
	
	//METRICS & LOGGING
	
	/*
	 * Output Host data to the log
	 */
	public void logState() {

		if (state == HostState.ON) {
			//logger output (human readable) 
			simulation.getLogger().debug("Host #" + getId() + 
					" CPU[" + (int)Math.round(resourceManager.getCpuInUse()) + "/" + resourceManager.getTotalCpu() + "] " +
					" BW[" + resourceManager.getAllocatedBandwidth() + "/" + resourceManager.getTotalBandwidth() + "] " +
					" MEM[" + resourceManager.getAllocatedMemory() + "/" + resourceManager.getTotalMemory() + "] " +
					" STORAGE[" + resourceManager.getAllocatedStorage() + "/" + resourceManager.getTotalStorage() + "] " +
					"Power[" + Utility.roundDouble(this.getCurrentPowerConsumption(), 2) + "W]");	
		} else {
			simulation.getLogger().debug("Host #" + getId() + " " + state);
		}
		
		//trace output
		simulation.getTraceLogger().info("#h," + getId() + "," + state + "," + (int)Math.round(resourceManager.getCpuInUse()) + "," +
				resourceManager.getAllocatedMemory() + "," +
				resourceManager.getAllocatedBandwidth() + "," +
				resourceManager.getAllocatedStorage() + "," + 
				Utility.roundDouble(this.getCurrentPowerConsumption(), 2));
		
		//log priv domain
		privDomainAllocation.getVm().logState();
		
//		if (id == 189) {
//			System.out.println("Host #" + getId() + 
//					" CPU[" + (int)Math.round(resourceManager.getCpuInUse()) + "/" + resourceManager.getTotalCpu() + "] " +
//					" BW[" + resourceManager.getAllocatedBandwidth() + "/" + resourceManager.getTotalBandwidth() + "] " +
//					" MEM[" + resourceManager.getAllocatedMemory() + "/" + resourceManager.getTotalMemory() + "] " +
//					" STORAGE[" + resourceManager.getAllocatedStorage() + "/" + resourceManager.getTotalStorage() + "] " +
//					"Power[" + Utility.roundDouble(this.getCurrentPowerConsumption(), 2) + "W]");
//		}
		
		for (VmAllocation vmAllocation : vmAllocations) {
			if (vmAllocation.getVm() != null) {
				vmAllocation.getVm().logState();
			} else {
				simulation.getLogger().debug("Empty Allocation CPU[" + vmAllocation.getCpu() + "]");
			}
		}
		
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
	
	public int getStorage() {	return storage; }
	
	public HostState getState() { return state; }
	
	public void setState(HostState state) { this.state = state; }
	
	public NetworkCard getDataNetworkCard() { return dataNetworkCard; }
	
	public void setDataNetworkCard(NetworkCard dataNetworkCard) { this.dataNetworkCard = dataNetworkCard; }
	
	public NetworkCard getMgmtNetworkCard() { return mgmtNetworkCard; }
	
	public void setMgmtNetworkCard(NetworkCard mgmtNetworkCard) { this.mgmtNetworkCard = mgmtNetworkCard; }
	
	public Rack getRack() {
		return rack;
	}
	
	public void setRack(Rack rack) {
		this.rack = rack;
	}
		
	public ResourceManager getResourceManager() {	return resourceManager;	 }
	
	public ResourceScheduler getResourceScheduler() { return resourceScheduler; } 
		
	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
		resourceManager.setHost(this);
	}
	
	public void setResourceScheduler(ResourceScheduler resourceScheduler) {
		this.resourceScheduler = resourceScheduler;
		resourceScheduler.setHost(this);
	}
	
	public ArrayList<VmAllocation> getVMAllocations() { return vmAllocations;	}
	
	public VmAllocation getVMAllocation(int vmId) {
		for (VmAllocation vmAlloc : vmAllocations) {
			if (vmAlloc.getVm() != null && vmAlloc.getVm().getId() == vmId) {
				return vmAlloc;
			}
		}
		return null;
	}
	
	public VmAllocation getPrivDomainAllocation() { 	return privDomainAllocation; }
	
	public ArrayList<VmAllocation> getMigratingIn() { return migratingIn;	}
	
	public ArrayList<VmAllocation> getMigratingOut() {	return migratingOut; }
	
	public ArrayList<VmAllocation> getStartingVms() { return startingVm; }
	
	public HostPowerModel getPowerModel() { 	return powerModel;	}
	
	public void setHostPowerModel(HostPowerModel powerModel) {
		this.powerModel = powerModel;
	}
	
	public long getTimeActive() { return timeActive; }
	
	public double getPowerConsumed() { return powerConsumed; }
	
	public double getAverageUtilization() { return utilizationSum / timeActive; }
	
	public boolean isShutdownPending() {	return powerOffAfterMigrations != null; }

	@Override
	public int hashCode() {
		return hashCode;
	}
	
	private int generateHashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, id);
		result = HashCodeUtil.hash(result, nCores);
		result = HashCodeUtil.hash(result, coreCapacity);
		result = HashCodeUtil.hash(result, memory);
		result = HashCodeUtil.hash(result, bandwidth);
		result = HashCodeUtil.hash(result, storage);
		return result;
	}
	
}
