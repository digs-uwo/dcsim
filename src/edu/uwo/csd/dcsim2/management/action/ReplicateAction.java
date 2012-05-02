package edu.uwo.csd.dcsim2.management.action;

import java.util.HashMap;
import java.util.Map;

import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.management.VMPlacementPolicy;
import edu.uwo.csd.dcsim2.vm.*;

public class ReplicateAction implements ManagementAction {

	private static Map<SimulationEventListener, Integer> replicateCount = new HashMap<SimulationEventListener, Integer>();
	
	private VMDescription vmDescription;
	private VMPlacementPolicy vmPlacementPolicy;
	
	private static void incrementReplicateCount(SimulationEventListener triggeringEntity) {
		int count = 0;
		if (replicateCount.containsKey(triggeringEntity)) {
			count = replicateCount.get(triggeringEntity);
		}
		replicateCount.put(triggeringEntity, count + 1);
	}

	public static Map<SimulationEventListener, Integer> getReplicateCount() {
		return replicateCount;
	}

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
		
		if (simulation.isRecordingMetrics())
			incrementReplicateCount(triggeringEntity);
	}
	
}
