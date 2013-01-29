package edu.uwo.csd.dcsim.vm;

import edu.uwo.csd.dcsim.application.Application;
import edu.uwo.csd.dcsim.common.Utility;
import edu.uwo.csd.dcsim.core.Simulation;

/**
 * A special VM that runs the VmmApplication for a Host 
 * 
 * @author Michael Tighe
 *
 */
public class PrivDomainVM extends VM {

	public PrivDomainVM(Simulation simulation, VMDescription vmDescription, Application application) {
		super(simulation, vmDescription, application);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void logState() {
		simulation.getLogger().debug("PRIV  CPU[" + Utility.roundDouble(resourcesScheduled.getCpu(), 2) + 
				"/" + vmAllocation.getCpu() + 
				"/" + Utility.roundDouble(application.getResourcesRequired().getCpu(), 2) + "] " + 
				"BW[" + Utility.roundDouble(resourcesScheduled.getBandwidth(), 2) + 
				"/" + vmAllocation.getBandwidth() + 
				"/" + Utility.roundDouble(application.getResourcesRequired().getBandwidth(), 2) + "] " + 
				"MEM[" + resourcesScheduled.getMemory() + 
				"/" + vmAllocation.getMemory() + "] " +
				"STORAGE[" + resourcesScheduled.getStorage() + 
				"/" + vmAllocation.getStorage() + "]");
	}
	
}
