package edu.uwo.csd.dcsim.management.policies;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.EventCallbackListener;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.events.*;
import edu.uwo.csd.dcsim.management.AutonomicManager;
import edu.uwo.csd.dcsim.management.Policy;
import edu.uwo.csd.dcsim.management.capabilities.HostManager;
import edu.uwo.csd.dcsim.management.events.InstantiateVmEvent;
import edu.uwo.csd.dcsim.management.events.MigrationCompleteEvent;
import edu.uwo.csd.dcsim.management.events.MigrationEvent;
import edu.uwo.csd.dcsim.management.events.ShutdownVmEvent;
import edu.uwo.csd.dcsim.vm.*;

public class HostOperationsPolicy extends Policy {

	protected AutonomicManager target;
	
	// Legacy.
	public HostOperationsPolicy() {
		addRequiredCapability(HostManager.class);
	}

	public HostOperationsPolicy(AutonomicManager target) {
		addRequiredCapability(HostManager.class);
		
		this.target = target;
	}

	public void execute(InstantiateVmEvent event) {
		HostManager hostManager = manager.getCapability(HostManager.class);
		Host host = hostManager.getHost();
		
		//verify that the application isn't complete before instantiating the VM (can happen if event is sent before app finishes, received after)
		if (event.getVMAllocationRequest().getVMDescription().getTask().getApplication().isComplete()) return;
		
		//if the host is set to shutdown upon completion of outgoing migrations, cancel this shutdown
		if (host.isShutdownPending()) {
			/*
			 * note that we directly make this call rather than send an event, as we want to
			 * avoid the host beginning its shutdown process before receiving the cancel event 
			 */
			host.cancelPendingShutdown(); 
		}

		SubmitVmEvent submitEvent = new SubmitVmEvent(host, event.getVMAllocationRequest());
		event.addEventInSequence(submitEvent);		
		simulation.sendEvent(submitEvent);
	}
	
	public void execute(MigrationEvent event) {
		HostManager hostManager = manager.getCapability(HostManager.class);
		Host host = hostManager.getHost();
		
		Host targetHost = event.getTargetHost();
		
		if (host.getVMAllocation(event.getVmId()) == null) {
			throw new RuntimeException(simulation.getSimulationTime() + " Attempted to migrate VM #" + event.getVmId() + " from Host #" + host.getId() + " to Host #" + targetHost.getId() +
					" - Failed as VM is not present on source host");
		}
		
		Vm vm = host.getVMAllocation(event.getVmId()).getVm();
		
		//create an allocation request for the target
		VmAllocationRequest vmAllocationRequest = new VmAllocationRequest(vm.getVMAllocation());
		
		//trigger migration in target host
		MigrateVmEvent migEvent = new MigrateVmEvent(host, targetHost, vmAllocationRequest, vm);
		event.addEventInSequence(migEvent); //defer completion of the original event until the MigrateVmEvent is complete
		
		// Add a callback listener to inform the target manager that the migration is complete.
		if (null != target)
			migEvent.addCallbackListener(new EventCallbackListener() {
				@Override
				public void eventCallback(Event e) {
					MigrateVmEvent event = (MigrateVmEvent) e;
					simulation.sendEvent(new MigrationCompleteEvent(target, event.getSource().getId(), event.getTargetHost().getId(), event.getVM().getId()));
				}
			});
		
		simulation.sendEvent(migEvent);
	}
	
	public void execute(ShutdownVmEvent event) {
		HostManager hostManager = manager.getCapability(HostManager.class);
		Host host = hostManager.getHost();
		VmAllocation vmAlloc = host.getVMAllocation(event.getVmId());
		
		//stop the VM and deallocate it from the host
		vmAlloc.getVm().stopTaskInstance();
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
