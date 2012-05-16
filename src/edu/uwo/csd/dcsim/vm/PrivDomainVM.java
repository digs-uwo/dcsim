package edu.uwo.csd.dcsim.vm;

import edu.uwo.csd.dcsim.application.Application;
import edu.uwo.csd.dcsim.common.Utility;
import edu.uwo.csd.dcsim.core.Simulation;

public class PrivDomainVM extends VM {

	public PrivDomainVM(Simulation simulation, VMDescription vmDescription, Application application) {
		super(simulation, vmDescription, application);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void logInfo() {
		simulation.getLogger().debug("PRIV  CPU[" + Utility.roundDouble(resourcesInUse.getCpu(), 2) + 
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
	
}
