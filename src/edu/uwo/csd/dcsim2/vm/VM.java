package edu.uwo.csd.dcsim2.vm;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.application.*;

public class VM extends SimulationEntity {

	private static Logger logger = Logger.getLogger(VM.class);
	
	private static int nextId = 1;
	
	private int id;
	private VMDescription vmDescription;
	private VirtualResources resourcesInUse; //current level of resource usage (not total used)
	private VMAllocation vmAllocation;
	
	/*
	 * Keep track of resources while calculating usage over the last time interval 
	 */
	private VirtualResources resourcesAvailable; //resources available to be consumed over this time interval
	private double maxCpuAvailable; //the maximum amount of CPU it is physically possible for this VM to use in the elapsed time interval
	private VirtualResources resourcesConsumed; //the resources consumed by the VM over the last time interval
	
	private Application application;

	public VM(VMDescription vmDescription, Application application) {
		this.id = nextId++;
		this.vmDescription = vmDescription;
		this.application = application;
	
		this.resourcesInUse = new VirtualResources(); //initialize resources in use TODO set memory and storage to base values
		
		vmAllocation = null;
	}
	
	public void beginScheduling() {
		resourcesAvailable = new VirtualResources();
		
		long timeElapsed = Simulation.getSimulation().getElapsedTime();
		
		//do not set CPU, it will be handled by the CPU scheduler and passed in to processWork()
		
		//calculate bandwidth available over the period
		resourcesAvailable.setBandwidth(vmAllocation.getBandwidthAllocation().getBandwidthAlloc() * (timeElapsed / 1000.0)); //bandwidth is in MB/s, time is in ms
		
		resourcesAvailable.setMemory(vmAllocation.getMemoryAllocation().getMemoryAlloc());
		resourcesAvailable.setStorage(vmAllocation.getStorageAllocation().getStorageAlloc());
		
		//reset resources consumed
		resourcesConsumed = new VirtualResources();
		
		//calculate a cap on the maximum CPU this VM could physically use
		maxCpuAvailable = vmDescription.getCores() * vmAllocation.getHost().getMaxCoreCapacity() * (timeElapsed / 1000.0);
		
		application.beginScheduling();
	}
	
	public double processWork(double cpuAvailable) {
		
		//ensure that the VM does not use more CPU than is possible for it to use
		cpuAvailable = Math.min(cpuAvailable, maxCpuAvailable);
		
		//set available CPU (note that any leftover CPU does not carry forward, the opportunity to use it has passed... is this correct?)
		resourcesAvailable.setCpu(cpuAvailable);
		
		//instruct the application to process work with available resources
		VirtualResources newResourcesConsumed = application.runApplication(resourcesAvailable);
		
		resourcesConsumed = resourcesConsumed.add(newResourcesConsumed);
		
		maxCpuAvailable -= newResourcesConsumed.getCpu();
		
		//logger.info("VM #" + getId() + " CPU[" + cpuAvailable + "," + newResourcesConsumed.getCpu() +  "]");
		
		return newResourcesConsumed.getCpu();
	}

	public void completeScheduling() {
		resourcesInUse = new VirtualResources();
		
		long elapsedTime = Simulation.getSimulation().getElapsedTime();
		
		resourcesInUse.setCpu(resourcesConsumed.getCpu() / (elapsedTime / 1000.0));
		resourcesInUse.setBandwidth(resourcesConsumed.getBandwidth() / (elapsedTime / 1000.0));
		
		resourcesInUse.setMemory(resourcesConsumed.getMemory());
		resourcesInUse.setStorage(resourcesConsumed.getStorage());
		
		application.completeScheduling();
	}
	
	public void logInfo() {
		/*
		 * Log VM usage information... should this be moved somewhere else? Should we log allocation alongside utilization?
		 */
		logger.info("VM #" + getId() + " CPU[" + Utility.roundDouble(resourcesInUse.getCpu(), 2) + "/" + vmAllocation.getCpuAllocation().getTotalAlloc() + "] " + 
				"BW[" + Utility.roundDouble(resourcesInUse.getBandwidth(), 2) + "] " + 
				"MEM[" + resourcesInUse.getMemory() + "] " +
				"STORAGE[" + resourcesInUse.getStorage() + "]");
		
		logger.info("	Application: CPU[" + Utility.roundDouble(application.getResourceInUse().getCpu(), 2) + "," + 
				Utility.roundDouble(application.getResourceDemand().getCpu(), 2) + "] " + 
				"BW[" + Utility.roundDouble(application.getResourceInUse().getBandwidth(), 2) + ", " + 
				Utility.roundDouble(application.getResourceDemand().getBandwidth(), 2) + "]");
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
