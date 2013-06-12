package edu.uwo.csd.dcsim.host.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.vm.VMAllocationRequest;

public class SubmitVmEvent extends Event {

	private VMAllocationRequest vmAllocationRequest;

	public SubmitVmEvent(Host target, VMAllocationRequest vmAllocationRequest) {
		super(target);
		 
		this.vmAllocationRequest = vmAllocationRequest;
	}
	
	public VMAllocationRequest getVmAllocationRequest() {
		return vmAllocationRequest;
	}

}
