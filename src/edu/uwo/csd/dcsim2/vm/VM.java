package edu.uwo.csd.dcsim2.vm;

import java.util.ArrayList;

import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.application.*;

public class VM extends SimulationEntity {

	private static int nextId = 1;
	
	private int id;
	private VMDescription vmDescription;
	private VirtualResources resourcesInUse;
	
	private Application application;
	
	private VMAllocation vmAllocation;
	
	public VM(VMDescription vmDescription, Application application) {
		this.id = nextId++;
		this.vmDescription = vmDescription;
		this.application = application;
	
		this.resourcesInUse = new VirtualResources(vmDescription.getVCores());
		
		vmAllocation = null;
	}
	
	public void processWork(int cpu) {
		
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
