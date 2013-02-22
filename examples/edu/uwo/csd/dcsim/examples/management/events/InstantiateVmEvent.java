package edu.uwo.csd.dcsim.examples.management.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.SimulationEventListener;
import edu.uwo.csd.dcsim.vm.VM;
import edu.uwo.csd.dcsim.vm.VMAllocationRequest;

public class InstantiateVmEvent extends Event {

	private VMAllocationRequest vmAllocationRequest;
	private VM vm;
	
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

}
