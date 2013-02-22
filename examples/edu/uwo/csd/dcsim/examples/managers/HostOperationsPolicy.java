package edu.uwo.csd.dcsim.examples.managers;

import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.management.Policy;

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
	
}
