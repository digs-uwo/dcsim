package edu.uwo.csd.dcsim.management.events;

import java.util.ArrayList;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.management.AutonomicManager;
import edu.uwo.csd.dcsim.vm.VMAllocationRequest;

public class VmPlacementEvent extends Event {
	
	private ArrayList<VMAllocationRequest> vmAllocationRequests;
	private ArrayList<VMAllocationRequest> failedRequests = new ArrayList<VMAllocationRequest>();
	
	public VmPlacementEvent(AutonomicManager target, ArrayList<VMAllocationRequest> vmAllocationRequests) {
		super(target);
		this.vmAllocationRequests = vmAllocationRequests;
	}	
	
	public VmPlacementEvent(AutonomicManager target, VMAllocationRequest vmAllocationRequest) {
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
