package edu.uwo.csd.dcsim.management.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.SimulationEventListener;
import edu.uwo.csd.dcsim.vm.VmAllocationRequest;

public class InstantiateVmEvent extends Event {

	private VmAllocationRequest vmAllocationRequest;
	private boolean failed = false;
	
	public InstantiateVmEvent(SimulationEventListener target, VmAllocationRequest vmAllocationRequest) {
		super(target);
		this.vmAllocationRequest = vmAllocationRequest;	
	}
	
	public VmAllocationRequest getVMAllocationRequest() {
		return vmAllocationRequest;
	}
	
	public void setFailed(boolean failed) {
		this.failed = failed;
	}
	
	public boolean failed() {
		return failed;
	}
	

}
