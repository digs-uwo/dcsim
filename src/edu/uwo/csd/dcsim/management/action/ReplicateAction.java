package edu.uwo.csd.dcsim.management.action;

import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.management.AutonomicManager;
import edu.uwo.csd.dcsim.management.events.VmPlacementEvent;
import edu.uwo.csd.dcsim.vm.*;

public class ReplicateAction extends ManagementAction {

	private VmDescription vmDescription;
	private AutonomicManager dcManager;
	
	public ReplicateAction(VmDescription vmDescription, AutonomicManager dcManager) {
		this.vmDescription = vmDescription;
		this.dcManager = dcManager;
	}
	
	public VmDescription getVMDescription() {
		return vmDescription;
	}
	
	public AutonomicManager getDCManager() {
		return dcManager;
	}

	public void execute(Simulation simulation, Object triggeringEntity) {
		VmAllocationRequest request = new VmAllocationRequest(vmDescription);
		
		VmPlacementEvent placementEvent = new VmPlacementEvent(dcManager, request);
		
		//add a callback listener to indicate this action is completed once the migration is finished
		placementEvent.addCallbackListener(new EventCallbackListener() {

			@Override
			public void eventCallback(Event e) {
				completeAction();
			}
			
		});
		simulation.sendEvent(placementEvent);		

	}
	
}
