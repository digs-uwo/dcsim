package edu.uwo.csd.dcsim2.management.action;

import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.core.metrics.AggregateMetric;
import edu.uwo.csd.dcsim2.management.VMPlacementPolicy;
import edu.uwo.csd.dcsim2.vm.*;

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
	
	public void execute(Simulation simulation, SimulationEventListener triggeringEntity) {
		VMAllocationRequest request = new VMAllocationRequest(vmDescription);
		vmPlacementPolicy.submitVM(request);
		
		if (simulation.isRecordingMetrics()) {
			AggregateMetric.getSimulationMetric(simulation, REPLICATE_COUNT_METRIC).addValue(1);
		}
	}
	
}
