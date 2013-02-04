package edu.uwo.csd.dcsim.host.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.vm.VMAllocationRequest;

public class SubmitVmEvent extends Event {

	VMAllocationRequest request;
	
	public SubmitVmEvent(Host target, VMAllocationRequest request) {
		super(target);

		this.request = request;
	}
	
	public VMAllocationRequest getRequest() {
		return request;
	}

}
