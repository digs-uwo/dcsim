package edu.uwo.csd.dcsim.examples.management;

import edu.uwo.csd.dcsim.examples.management.capabilities.HostManager;
import edu.uwo.csd.dcsim.examples.management.events.*;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.events.*;
import edu.uwo.csd.dcsim.host.events.PowerStateEvent.PowerState;
import edu.uwo.csd.dcsim.management.Policy;
import edu.uwo.csd.dcsim.vm.*;

public class HostOperationsPolicy extends Policy {

	public HostOperationsPolicy() {
		super(HostManager.class);
	}
	
	public void execute(InstantiateVmEvent event) {
		HostManager hostManager = manager.getCapability(HostManager.class);
		Host host = hostManager.getHost();
		
		//if the host is set to shutdown upon completion of outgoing migrations, cancel this shutdown
		if (host.isShutdownPending()) {
			/*
			 * note that we directly make this call rather than send an event, as we want to
			 * avoid the host beginning its shutdown process before receiving the cancel event 
			 */
			host.cancelPendingShutdown(); 
		}
		
		host.submitVM(event.getVMAllocationRequest());
	}
	
	public void execute(MigrationEvent event) {
		HostManager hostManager = manager.getCapability(HostManager.class);
		Host host = hostManager.getHost();
		
		Host targetHost = event.getTargetHost();
		VM vm = host.getVMAllocation(event.getVmId()).getVm();
		
		//create an allocation request for the target
		VMAllocationRequest vmAllocationRequest = new VMAllocationRequest(vm.getVMAllocation());
		
		//trigger migration in target host
		targetHost.sendMigrationEvent(vmAllocationRequest, vm, host);
		
		if (event.shutdownIfEmpty()) {
			//check if there is only one VM left (vm has not started migrating yet, so it is still present)
			if (host.getVMAllocations().size() == 1) {
				simulation.sendEvent(new PowerStateEvent(host, PowerState.POWER_OFF));
			}
		}
		
	}
	
}
