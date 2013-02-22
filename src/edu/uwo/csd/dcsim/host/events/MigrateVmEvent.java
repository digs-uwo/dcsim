package edu.uwo.csd.dcsim.host.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.vm.*;

public class MigrateVmEvent extends Event {

	boolean complete;
	Host source;
	Host target;
	VMAllocationRequest vmAllocationRequest;
	VM vm;
	
	VMAllocation vmAllocation;
	
	/**
	 * Creates MigrateVmEvent triggering the start of a migration, as indicated by passing a VMAllocationRequest instead of a VMAllocation
	 * @param source
	 * @param target
	 * @param vmAllocationRequest
	 * @param vm
	 */
	public MigrateVmEvent(Host source, Host target, VMAllocationRequest vmAllocationRequest, VM vm) {
		super(target);
		
		this.source = source;
		this.target = target;
		this.vmAllocationRequest = vmAllocationRequest;
		this.vm = vm;
		complete = false;
	}
	
	/**
	 * Creates a MigrateVmEvent completing a migration, as indicated by passing a VMAllocation instead of a VMAllocationRequest
	 * @param source
	 * @param target
	 * @param vmAllocation
	 * @param vm
	 */
	public MigrateVmEvent(Host source, Host target, VMAllocation vmAllocation, VM vm) {
		super(target);
		
		this.source = source;
		this.target = target;
		this.vmAllocation = vmAllocation;
		this.vm = vm;
		complete = true;
	}
	
	public boolean isComplete() {
		return complete;
	}
	
	public Host getSource() {
		return source;
	}
	
	public Host getTargetHost() {
		return target;
	}
	
	public VMAllocationRequest getVMAllocationRequest() {
		return vmAllocationRequest;
	}
	
	public VM getVM() {
		return vm;
	}
	
	public void setVMAllocation(VMAllocation vmAllocation) {
		this.vmAllocation = vmAllocation;
	}
	
	public VMAllocation getVMAllocation() {
		return vmAllocation;
	}

}
