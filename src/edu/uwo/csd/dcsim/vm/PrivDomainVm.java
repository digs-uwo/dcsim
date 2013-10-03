package edu.uwo.csd.dcsim.vm;

import edu.uwo.csd.dcsim.application.TaskInstance;
import edu.uwo.csd.dcsim.common.Utility;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.host.Host;

/**
 * A special VM that runs the VmmApplication for a Host 
 * 
 * @author Michael Tighe
 *
 */
public class PrivDomainVm extends Vm {

	public PrivDomainVm(Simulation simulation, VmDescription vmDescription, TaskInstance application) {
		super(simulation, vmDescription, application);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void logState() {
		if (getVMAllocation().getHost().getState() == Host.HostState.ON) {
			simulation.getLogger().debug("PRIV  CPU[" + Utility.roundDouble(resourcesScheduled.getCpu(), 2) + 
					"/" + vmAllocation.getCpu() + 
					"/" + Utility.roundDouble(taskInstance.getResourceDemand().getCpu(), 2) + "] " + 
					"BW[" + Utility.roundDouble(resourcesScheduled.getBandwidth(), 2) + 
					"/" + vmAllocation.getBandwidth() + 
					"/" + Utility.roundDouble(taskInstance.getResourceDemand().getBandwidth(), 2) + "] " + 
					"MEM[" + resourcesScheduled.getMemory() + 
					"/" + vmAllocation.getMemory() + "] " +
					"STORAGE[" + resourcesScheduled.getStorage() + 
					"/" + vmAllocation.getStorage() + "]");
		}
		
		//trace output
		simulation.getTraceLogger().info("#vp," + getId() + "," + vmAllocation.getHost().getId() + "," + 
				Utility.roundDouble(resourcesScheduled.getCpu(), 2) + "," + Utility.roundDouble(taskInstance.getResourceDemand().getCpu(), 2) + "," + 
				Utility.roundDouble(resourcesScheduled.getBandwidth(), 2) + "," + Utility.roundDouble(taskInstance.getResourceDemand().getBandwidth(), 2) + "," + 
				resourcesScheduled.getMemory() + "," + vmAllocation.getMemory() + "," +
				resourcesScheduled.getStorage() + "," + vmAllocation.getStorage());
	}
	
}
