package edu.uwo.csd.dcsim.management.action;

import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.core.metrics.ActionCountMetric;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.events.PowerStateEvent;
import edu.uwo.csd.dcsim.host.events.PowerStateEvent.PowerState;
import edu.uwo.csd.dcsim.vm.*;

public class ShutdownVmAction implements ManagementAction {

	private static final String SHUTDOWN_COUNT_METRIC = "shutdownCount";
	
	private VM vm;
	
	public ShutdownVmAction(VM vm) {
		this.vm = vm;
	}
	
	public VM getVm() {
		return vm;
	}
		
	public void execute(Simulation simulation, Object triggeringEntity) {
		
		VMAllocation vmAllocation = vm.getVMAllocation();
		Host host = vmAllocation.getHost();
		
		if (host.getMigratingOut().contains(vm))
			throw new RuntimeException("Tried to shutdown migrating VM #" + vm.getId() + ". Operation not allowed in simulation.");
		
		host.deallocate(vmAllocation);
		vm.stopApplication();
		
		if (simulation.isRecordingMetrics()) {
			ActionCountMetric.getMetric(simulation, SHUTDOWN_COUNT_METRIC).incrementCount();
		}
		
		//if the host will no longer contain any VMs, instruct it to shut down
		if (host.getVMAllocations().size() == 0) {
			simulation.sendEvent(new PowerStateEvent(host, PowerState.POWER_OFF));
		}
	}
	
}
