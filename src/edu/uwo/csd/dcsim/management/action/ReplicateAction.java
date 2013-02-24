package edu.uwo.csd.dcsim.management.action;

import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.core.metrics.ActionCountMetric;
import edu.uwo.csd.dcsim.management.AutonomicManager;
import edu.uwo.csd.dcsim.management.events.VmPlacementEvent;
import edu.uwo.csd.dcsim.vm.*;

public class ReplicateAction implements ManagementAction {

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
		
		simulation.sendEvent(new VmPlacementEvent(dcManager, request));		

		if (simulation.isRecordingMetrics()) {
			ActionCountMetric.getMetric(simulation, REPLICATE_COUNT_METRIC).incrementCount();
		}
	}
	
}
