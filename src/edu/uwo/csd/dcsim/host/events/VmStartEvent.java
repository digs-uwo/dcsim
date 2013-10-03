package edu.uwo.csd.dcsim.host.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.vm.VmAllocation;

public class VmStartEvent extends Event {

	private VmAllocation vmAllocation;
	
	public VmStartEvent(Host target, VmAllocation vmAllocation) {
		super(target);
		
		this.vmAllocation = vmAllocation;
		
	}
	
	public VmAllocation getVmAllocation() {
		return vmAllocation;
	}

}
