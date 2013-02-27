package edu.uwo.csd.dcsim.management.policies;

import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.events.*;
import edu.uwo.csd.dcsim.host.events.PowerStateEvent.PowerState;
import edu.uwo.csd.dcsim.management.Policy;
import edu.uwo.csd.dcsim.management.capabilities.HostManager;
import edu.uwo.csd.dcsim.management.events.InstantiateVmEvent;
import edu.uwo.csd.dcsim.management.events.MigrationEvent;
import edu.uwo.csd.dcsim.management.events.ShutdownVmEvent;
import edu.uwo.csd.dcsim.vm.*;

public class HostOperationsPolicy extends Policy {

	public HostOperationsPolicy() {
		addRequiredCapability(HostManager.class);
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
		
		event.setVM(host.submitVM(event.getVMAllocationRequest()));
		
	}
	
	public void execute(MigrationEvent event) {
		HostManager hostManager = manager.getCapability(HostManager.class);
		Host host = hostManager.getHost();
		
		Host targetHost = event.getTargetHost();
		
		VM vm = host.getVMAllocation(event.getVmId()).getVm();
		
		//create an allocation request for the target
		VMAllocationRequest vmAllocationRequest = new VMAllocationRequest(vm.getVMAllocation());
		
		//trigger migration in target host
		MigrateVmEvent migEvent = new MigrateVmEvent(host, targetHost, vmAllocationRequest, vm);
		event.addEventInSequence(migEvent); //defer completion of the original event until the MigrateVmEvent is complete
		simulation.sendEvent(migEvent);
		
		if (event.shutdownIfEmpty()) {
			//check if there is only one VM left (vm has not started migrating yet, so it is still present)
			if (host.getVMAllocations().size() == 1) {
				simulation.sendEvent(new PowerStateEvent(host, PowerState.POWER_OFF));
			}
		}
		
	}
	
	public void execute(ShutdownVmEvent event) {
		HostManager hostManager = manager.getCapability(HostManager.class);
		Host host = hostManager.getHost();
		VMAllocation vmAlloc = host.getVMAllocation(event.getVmId());
		
		//stop the VM and deallocate it from the host
		vmAlloc.getVm().stopApplication();
		host.deallocate(vmAlloc);
	}

	@Override
	public void onInstall() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onManagerStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onManagerStop() {
		// TODO Auto-generated method stub
		
	}
	
}
