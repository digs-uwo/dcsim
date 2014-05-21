package edu.uwo.csd.dcsim.host.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.vm.VmAllocation;
import edu.uwo.csd.dcsim.vm.VmAllocationRequest;

public class SubmitVmEvent extends Event {

	private VmAllocationRequest vmAllocationRequest;
	private VmAllocation vmAllocation;		// Completed after processing the event.

	public SubmitVmEvent(Host target, VmAllocationRequest vmAllocationRequest) {
		super(target);
		 
		this.vmAllocationRequest = vmAllocationRequest;
	}
	
	public VmAllocationRequest getVmAllocationRequest() {
		return vmAllocationRequest;
	}
	
	public VmAllocation getVmAllocation() {
		return vmAllocation;
	}
	
	public void setVmAllocation(VmAllocation vmAllocation) {
		this.vmAllocation = vmAllocation;
	}

}
