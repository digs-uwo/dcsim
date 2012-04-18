package edu.uwo.csd.dcsim2.management.action;

import java.util.HashMap;
import java.util.Map;

import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.host.Host;
import edu.uwo.csd.dcsim2.management.VMPlacementPolicy;
import edu.uwo.csd.dcsim2.vm.*;

public class ShutdownVmAction implements ManagementAction {

	private static Map<SimulationEntity, Integer> shutdownCount = new HashMap<SimulationEntity, Integer>();
	
	private VM vm;
	
	private static void incrementShutdownCount(SimulationEntity triggeringEntity) {
		int count = 0;
		if (shutdownCount.containsKey(triggeringEntity)) {
			count = shutdownCount.get(triggeringEntity);
		}
		shutdownCount.put(triggeringEntity, count + 1);
	}

	public static Map<SimulationEntity, Integer> getShutdownCount() {
		return shutdownCount;
	}

	public ShutdownVmAction(VM vm) {
		this.vm = vm;
	}
	
	public VM getVm() {
		return vm;
	}
		
	public void execute(SimulationEntity triggeringEntity) {
		
		VMAllocation vmAllocation = vm.getVMAllocation();
		Host host = vmAllocation.getHost();
		
		if (host.getMigratingOut().contains(vm))
			throw new RuntimeException("Tried to shutdown migrating VM #" + vm.getId() + ". Operation not allowed in simulation.");
		
		host.deallocate(vmAllocation);
		vm.stopApplication();
		
		incrementShutdownCount(triggeringEntity);
		
		//if the host will no longer contain any VMs, instruct it to shut down
		if (host.getVMAllocations().size() == 0) {
			Simulation.getInstance().sendEvent(
					new Event(Host.HOST_POWER_OFF_EVENT,
							Simulation.getInstance().getSimulationTime(),
							triggeringEntity,
							host)
					);
		}
	}
	
}
