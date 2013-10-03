package edu.uwo.csd.dcsim.host.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.vm.VmAllocationRequest;

public class SubmitVmEvent extends Event {

	private VmAllocationRequest vmAllocationRequest;

	public SubmitVmEvent(Host target, VmAllocationRequest vmAllocationRequest) {
		super(target);
		 
		this.vmAllocationRequest = vmAllocationRequest;
	}
	
	public VmAllocationRequest getVmAllocationRequest() {
		return vmAllocationRequest;
	}

}
