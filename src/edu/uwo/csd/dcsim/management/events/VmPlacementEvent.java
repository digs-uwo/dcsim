package edu.uwo.csd.dcsim.management.events;

import java.util.ArrayList;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.management.AutonomicManager;
import edu.uwo.csd.dcsim.vm.VmAllocationRequest;

public class VmPlacementEvent extends Event {
	
	private ArrayList<VmAllocationRequest> vmAllocationRequests;
	private ArrayList<VmAllocationRequest> failedRequests = new ArrayList<VmAllocationRequest>();
	
	public VmPlacementEvent(AutonomicManager target, ArrayList<VmAllocationRequest> vmAllocationRequests) {
		super(target);
		this.vmAllocationRequests = vmAllocationRequests;
	}	
	
	public VmPlacementEvent(AutonomicManager target, VmAllocationRequest vmAllocationRequest) {
		super(target);
		this.vmAllocationRequests = new ArrayList<VmAllocationRequest>();
		vmAllocationRequests.add(vmAllocationRequest);
	}
	
	public ArrayList<VmAllocationRequest> getVMAllocationRequests() {
		return vmAllocationRequests;
	}
	
	public ArrayList<VmAllocationRequest> getFailedRequests() {
		return failedRequests;
	}
	
	public void addFailedRequest(VmAllocationRequest failedRequest) {
		failedRequests.add(failedRequest);
	}
	
}
