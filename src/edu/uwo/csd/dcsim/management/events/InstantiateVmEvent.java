package edu.uwo.csd.dcsim.management.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.SimulationEventListener;
import edu.uwo.csd.dcsim.vm.VM;
import edu.uwo.csd.dcsim.vm.VMAllocationRequest;

public class InstantiateVmEvent extends Event {

	private VMAllocationRequest vmAllocationRequest;
	private VM vm;
	private boolean failed = false;
	
	public InstantiateVmEvent(SimulationEventListener target, VMAllocationRequest vmAllocationRequest) {
		super(target);
		this.vmAllocationRequest = vmAllocationRequest;	
	}
	
	public VMAllocationRequest getVMAllocationRequest() {
		return vmAllocationRequest;
	}
	
	public VM getVM() {
		return vm;
	}
	
	public void setVM(VM vm) {
		this.vm = vm;
	}
	
	public void setFailed(boolean failed) {
		this.failed = failed;
	}
	
	public boolean failed() {
		return failed;
	}

}
