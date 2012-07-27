package edu.uwo.csd.dcsim.management.action;

import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.core.metrics.ActionCountMetric;
import edu.uwo.csd.dcsim.management.VMPlacementPolicy;
import edu.uwo.csd.dcsim.vm.*;

public class ReplicateAction implements ManagementAction {

	private static final String REPLICATE_COUNT_METRIC = "replicationCount";
	
	private VMDescription vmDescription;
	private VMPlacementPolicy vmPlacementPolicy;
	
	public ReplicateAction(VMDescription vmDescription, VMPlacementPolicy vmPlacementPolicy) {
		this.vmDescription = vmDescription;
		this.vmPlacementPolicy = vmPlacementPolicy;
	}
	
	public VMDescription getVMDescription() {
		return vmDescription;
	}
	
	public VMPlacementPolicy getVMPlacementPolicy() {
		return vmPlacementPolicy;
	}
	
	public void execute(Simulation simulation, Object triggeringEntity) {
		VMAllocationRequest request = new VMAllocationRequest(vmDescription);
		vmPlacementPolicy.submitVM(request);
		
		if (simulation.isRecordingMetrics()) {
			ActionCountMetric.getMetric(simulation, REPLICATE_COUNT_METRIC).incrementCount();
		}
	}
	
}
