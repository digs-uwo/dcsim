package edu.uwo.csd.dcsim.examples.management.events;

import java.util.ArrayList;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.SimulationEventListener;
import edu.uwo.csd.dcsim.vm.VMAllocationRequest;

public class VmPlacementEvent extends Event {
	
	private ArrayList<VMAllocationRequest> vmAllocationRequests;
	private ArrayList<VMAllocationRequest> failedRequests = new ArrayList<VMAllocationRequest>();
	
	public VmPlacementEvent(SimulationEventListener target, ArrayList<VMAllocationRequest> vmAllocationRequests) {
		super(target);
		this.vmAllocationRequests = vmAllocationRequests;
	}	
	
	public VmPlacementEvent(SimulationEventListener target, VMAllocationRequest vmAllocationRequest) {
		super(target);
		this.vmAllocationRequests = new ArrayList<VMAllocationRequest>();
		vmAllocationRequests.add(vmAllocationRequest);
	}
	
	public ArrayList<VMAllocationRequest> getVMAllocationRequests() {
		return vmAllocationRequests;
	}
	
	public ArrayList<VMAllocationRequest> getFailedRequests() {
		return failedRequests;
	}
	
	public void addFailedRequest(VMAllocationRequest failedRequest) {
		failedRequests.add(failedRequest);
	}
	
}
