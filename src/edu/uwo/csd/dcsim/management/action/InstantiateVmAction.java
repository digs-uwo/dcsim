package edu.uwo.csd.dcsim.management.action;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.EventCallbackListener;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.events.PowerStateEvent;
import edu.uwo.csd.dcsim.host.events.PowerStateEvent.PowerState;
import edu.uwo.csd.dcsim.management.HostData;
import edu.uwo.csd.dcsim.management.events.InstantiateVmEvent;
import edu.uwo.csd.dcsim.management.events.VmPlacementEvent;
import edu.uwo.csd.dcsim.vm.VMAllocationRequest;

public class InstantiateVmAction extends ManagementAction {

	private HostData target;
	private VMAllocationRequest vmAllocationRequest;
	private VmPlacementEvent placementEvent;
	
	public InstantiateVmAction(HostData target, VMAllocationRequest vmAllocationRequest, VmPlacementEvent placementEvent) {
		this.target = target;
		this.vmAllocationRequest = vmAllocationRequest;
		this.placementEvent = placementEvent;
	}
	
	@Override
	public void execute(Simulation simulation, Object triggeringEntity) {
		//if the host is not ON or POWERING_ON, then send an event to power on the host
		if (target.getCurrentStatus().getState() != Host.HostState.ON && target.getCurrentStatus().getState() != Host.HostState.POWERING_ON) {
			simulation.sendEvent(new PowerStateEvent(target.getHost(), PowerState.POWER_ON));
		}
		
		InstantiateVmEvent instantiateEvent = new InstantiateVmEvent(target.getHostManager(), vmAllocationRequest);
		if (placementEvent != null) placementEvent.addEventInSequence(instantiateEvent);		
		
		//add a callback listener to indicate this action is completed once the instantiation is finished
		instantiateEvent.addCallbackListener(new EventCallbackListener() {

			@Override
			public void eventCallback(Event e) {
				completeAction();
			}
			
		});
		
		simulation.sendEvent(instantiateEvent);
		
	}

}
