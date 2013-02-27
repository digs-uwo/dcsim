package edu.uwo.csd.dcsim.management.action;

import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.core.metrics.CountMetric;
import edu.uwo.csd.dcsim.management.AutonomicManager;
import edu.uwo.csd.dcsim.management.events.VmPlacementEvent;
import edu.uwo.csd.dcsim.vm.*;

public class ReplicateAction extends ManagementAction {

	private static final String REPLICATE_COUNT_METRIC = "replicationCount";
	
	private VMDescription vmDescription;
	private AutonomicManager dcManager;
	
	public ReplicateAction(VMDescription vmDescription, AutonomicManager dcManager) {
		this.vmDescription = vmDescription;
		this.dcManager = dcManager;
	}
	
	public VMDescription getVMDescription() {
		return vmDescription;
	}
	
	public AutonomicManager getDCManager() {
		return dcManager;
	}

	public void execute(Simulation simulation, Object triggeringEntity) {
		VMAllocationRequest request = new VMAllocationRequest(vmDescription);
		
		VmPlacementEvent placementEvent = new VmPlacementEvent(dcManager, request);
		
		//add a callback listener to indicate this action is completed once the migration is finished
		placementEvent.addCallbackListener(new EventCallbackListener() {

			@Override
			public void eventCallback(Event e) {
				completeAction();
			}
			
		});
		simulation.sendEvent(placementEvent);		

		if (simulation.isRecordingMetrics()) {
			CountMetric.getMetric(simulation, REPLICATE_COUNT_METRIC).incrementCount();
		}
	}
	
}
