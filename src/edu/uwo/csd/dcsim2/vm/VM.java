package edu.uwo.csd.dcsim2.vm;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.application.*;

public class VM extends SimulationEntity {

	private static Logger logger = Logger.getLogger(VM.class);
	
	private static int nextId = 1;
	
	private int id;
	private VMDescription vmDescription;
	private VirtualResources resourcesInUse;
	
	private VirtualResources resourcesAvailable;
	private VirtualResources resourcesConsumed;
	
	private Application application;
	
	private VMAllocation vmAllocation;
	
	public VM(VMDescription vmDescription, Application application) {
		this.id = nextId++;
		this.vmDescription = vmDescription;
		this.application = application;
	
		this.resourcesInUse = new VirtualResources();
		
		vmAllocation = null;
	}
	
	public void updateResourcesAvailable() {
		resourcesAvailable = new VirtualResources();
		
		//do not set CPU, it will be handled by the CPU scheduler and passed in to processWork()
		
		//calculate bandwidth available over the period
		long timeElapsed = Simulation.getSimulation().getSimulationTime() - Simulation.getSimulation().getLastUpdate();
		resourcesAvailable.setBandwidth((int)(vmAllocation.getBandwidthAllocation().getBandwidthAlloc() * timeElapsed / 1000)); //bandwidth is in MB/s, time is in ms
		
		resourcesAvailable.setMemory(vmAllocation.getMemoryAllocation().getMemoryAlloc());
		resourcesAvailable.setStorage(vmAllocation.getStorageAllocation().getStorageAlloc());
		
		//reset resources consumed
		resourcesConsumed = new VirtualResources();
	}
	
	public void processWork(int cpuAvailable) {
		
		logger.info("VM #" + getId() + " processing " + cpuAvailable);
		
		//set available CPU (note that any leftover CPU does not carry forward, the opportunity to use it has passed... is this correct?)
		resourcesAvailable.setCpu(cpuAvailable);
		
		//instruct the application to process work with available resources
		resourcesConsumed = resourcesConsumed.add(application.processWork(resourcesAvailable));
	}

	public void updateResourcesInUse() {
		resourcesInUse = new VirtualResources();
		
		long elapsedTime = Simulation.getSimulation().getSimulationTime() - Simulation.getSimulation().getLastUpdate();
		
		resourcesInUse.setCpu((int)(resourcesConsumed.getCpu() / (elapsedTime / 1000.0)));
		resourcesInUse.setBandwidth((int)(resourcesConsumed.getBandwidth() / (elapsedTime / 1000.0)));
		
		resourcesInUse.setMemory(resourcesConsumed.getMemory());
		resourcesInUse.setStorage(resourcesConsumed.getStorage());
		
		/*
		 * Log VM usage information... should this be moved somewhere else? Should we log allocation alongside utilization?
		 */
		logger.info("VM #" + getId() + " Utilization - CPU[" + resourcesInUse.getCpu() + "] BW[" + resourcesInUse.getBandwidth() + "] MEM[" + resourcesInUse.getMemory() + "] STORAGE[" + resourcesInUse.getStorage() + "]");
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
