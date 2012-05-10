package edu.uwo.csd.dcsim2.management.action;

import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.core.metrics.AggregateMetric;
import edu.uwo.csd.dcsim2.host.Host;
import edu.uwo.csd.dcsim2.vm.*;

public class ShutdownVmAction implements ManagementAction {

	private static final String SHUTDOWN_COUNT_METRIC = "shutdownCount";
	
	private VM vm;
	
	public ShutdownVmAction(VM vm) {
		this.vm = vm;
	}
	
	public VM getVm() {
		return vm;
	}
		
	public void execute(Simulation simulation, SimulationEventListener triggeringEntity) {
		
		VMAllocation vmAllocation = vm.getVMAllocation();
		Host host = vmAllocation.getHost();
		
		if (host.getMigratingOut().contains(vm))
			throw new RuntimeException("Tried to shutdown migrating VM #" + vm.getId() + ". Operation not allowed in simulation.");
		
		host.deallocate(vmAllocation);
		vm.stopApplication();
		
		if (simulation.isRecordingMetrics()) {
			AggregateMetric.getSimulationMetric(simulation, SHUTDOWN_COUNT_METRIC).addValue(1);
		}
		
		//if the host will no longer contain any VMs, instruct it to shut down
		if (host.getVMAllocations().size() == 0) {
			simulation.sendEvent(
					new Event(Host.HOST_POWER_OFF_EVENT,
							simulation.getSimulationTime(),
							triggeringEntity,
							host)
					);
		}
	}
	
}
