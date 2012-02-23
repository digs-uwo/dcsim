package edu.uwo.csd.dcsim2.vm;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim2.DCSim2;
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
		//set available CPU (note that any leftover CPU does not carry forward, the opportunity to use it has passed... is this correct?)
		resourcesAvailable.setCpu(cpuAvailable);
		
		//instruct the application to process work with available resources
		resourcesConsumed = resourcesConsumed.add(application.processWork(resourcesAvailable));
	}

	public void updateResourcesInUse() {
		resourcesInUse = new VirtualResources();
		
		long elapsedSeconds = (Simulation.getSimulation().getSimulationTime() - Simulation.getSimulation().getLastUpdate()) / 1000;
		
		resourcesInUse.setCpu((int)(resourcesConsumed.getCpu() / elapsedSeconds));
		resourcesInUse.setBandwidth((int)(resourcesConsumed.getBandwidth() / elapsedSeconds));
		
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
