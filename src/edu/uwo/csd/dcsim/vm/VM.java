package edu.uwo.csd.dcsim.vm;

import edu.uwo.csd.dcsim.application.*;
import edu.uwo.csd.dcsim.common.Utility;
import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.core.metrics.VmCountMetric;

/**
 * Represents a Virtual Machine, running an Application. Must be contained within a VMAllocation on a Host
 * 
 * @author Michael Tighe
 *
 */
public class VM implements SimulationEventListener {

	private static final String VM_COUNT_METRIC = "vmCount";
	
	Simulation simulation;
	int id;
	VMDescription vmDescription;
	VirtualResources resourcesInUse; //current level of resource usage (not total used)
	VMAllocation vmAllocation; //the allocation this VM is running within
	
	/*
	 * Keep track of resources while calculating usage over the last time interval 
	 */
	VirtualResources resourcesAvailable; //resources available to be consumed over this time interval
	double maxCpuAvailable; //the maximum amount of CPU it is physically possible for this VM to use in the elapsed time interval
	VirtualResources resourcesConsumed; //the resources consumed by the VM over the last time interval
	
	Application application;
	
	public VM(Simulation simulation, VMDescription vmDescription, Application application) {
		this.simulation = simulation;
		this.id = simulation.nextId(VM.class.toString());
		this.vmDescription = vmDescription;
		this.application = application;
		application.setVM(this);
	
		this.resourcesInUse = new VirtualResources(); //initialize resources in use TODO set memory and storage to base values
		
		vmAllocation = null;
	}
	
	public void prepareExecution() {
		resourcesAvailable = new VirtualResources();
		
		long timeElapsed = simulation.getElapsedTime();
		
		//do not set CPU, it will be handled by the CPU scheduler and passed in to processWork()
		
		//calculate bandwidth available over the period
		resourcesAvailable.setBandwidth(vmAllocation.getBandwidth() * (timeElapsed / 1000.0)); //bandwidth is in MB/s, time is in ms
		
		resourcesAvailable.setMemory(vmAllocation.getMemory());
		resourcesAvailable.setStorage(vmAllocation.getStorage());
		
		//reset resources consumed
		resourcesConsumed = new VirtualResources();
		
		//calculate a cap on the maximum CPU this VM could physically use
		maxCpuAvailable = vmDescription.getCores() * vmAllocation.getHost().getCoreCapacity() * (timeElapsed / 1000.0);
		
		application.prepareExecution();
	}
	
	public double execute(double cpuAvailable) {
		
		//ensure that the VM does not use more CPU than is possible for it to use
		cpuAvailable = Math.min(cpuAvailable, maxCpuAvailable);
		
		//set available CPU (note that any leftover CPU does not carry forward, the opportunity to use it has passed... is this correct?)
		resourcesAvailable.setCpu(cpuAvailable);
		
		//update the resource demand of the application
		application.updateResourceDemand();
		
		//instruct the application to process work with available resources
		VirtualResources newResourcesConsumed = application.execute(resourcesAvailable);
		
		resourcesConsumed = resourcesConsumed.add(newResourcesConsumed);
		
		maxCpuAvailable -= newResourcesConsumed.getCpu();
		
		//logger.info("VM #" + getId() + " CPU[" + cpuAvailable + "," + newResourcesConsumed.getCpu() +  "]");
		
		return newResourcesConsumed.getCpu();
	}

	public void completeExecution() {
		
		resourcesInUse = new VirtualResources();
		
		long elapsedTime = simulation.getElapsedTime();
		
		resourcesInUse.setCpu(resourcesConsumed.getCpu() / (elapsedTime / 1000.0));
		resourcesInUse.setBandwidth(resourcesConsumed.getBandwidth() / (elapsedTime / 1000.0));
		
		resourcesInUse.setMemory(resourcesConsumed.getMemory());
		resourcesInUse.setStorage(resourcesConsumed.getStorage());
		
		//update the resource demand of the application
		application.updateResourceDemand();
		
		application.completeExecution();
	}
	
	public void startApplication() {
		vmDescription.getApplicationFactory().startApplication(application);
	}
	
	public void stopApplication() {
		vmDescription.getApplicationFactory().stopApplication(application);
	}
	
	public void updateMetrics() {
		
		VmCountMetric.getMetric(simulation, VM_COUNT_METRIC).incrementVmCount();
		
		application.updateMetrics();
	}
	
	public void logInfo() {
		simulation.getLogger().debug("VM #" + getId() + " CPU[" + Utility.roundDouble(resourcesInUse.getCpu(), 2) + 
				"/" + vmAllocation.getCpu() + 
				"/" + Utility.roundDouble(application.getResourceDemand().getCpu(), 2) + "] " + 
				"BW[" + Utility.roundDouble(resourcesInUse.getBandwidth(), 2) + 
				"/" + vmAllocation.getBandwidth() + 
				"/" + Utility.roundDouble(application.getResourceDemand().getBandwidth(), 2) + "] " + 
				"MEM[" + resourcesInUse.getMemory() + 
				"/" + vmAllocation.getMemory() + "] " +
				"STORAGE[" + resourcesInUse.getStorage() + 
				"/" + vmAllocation.getStorage() + "]");
	}
	
	public boolean isMigrating() {
		return vmAllocation.getHost().isMigrating(this);
	}
	
	public boolean isPendingMigration() {
		return vmAllocation.getHost().isPendingMigration(this);
	}
	
	public int getId() {
		return id;
	}
	
	public Application getApplication() {
		return application;
	}
	
	public VMDescription getVMDescription() {
		return vmDescription;
	}
	
	public VMAllocation getVMAllocation() {
		return vmAllocation;
	}
	
	public void setVMAllocation(VMAllocation vmAllocation) {
		this.vmAllocation = vmAllocation;
	}
	
	public VirtualResources getResourcesInUse() {
		return resourcesInUse;
	}
	
	@Override
	public void handleEvent(Event e) {
		// TODO Auto-generated method stub
		
	}


}
