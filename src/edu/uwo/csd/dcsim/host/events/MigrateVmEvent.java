package edu.uwo.csd.dcsim.host.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.vm.*;

public class MigrateVmEvent extends Event {

	boolean complete;
	Host source;
	Host target;
	VmAllocationRequest vmAllocationRequest;
	Vm vm;
	
	VmAllocation vmAllocation;
	
	/**
	 * Creates MigrateVmEvent triggering the start of a migration, as indicated by passing a VMAllocationRequest instead of a VMAllocation
	 * @param source
	 * @param target
	 * @param vmAllocationRequest
	 * @param vm
	 */
	public MigrateVmEvent(Host source, Host target, VmAllocationRequest vmAllocationRequest, Vm vm) {
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
	public MigrateVmEvent(Host source, Host target, VmAllocation vmAllocation, Vm vm) {
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
	
	public VmAllocationRequest getVMAllocationRequest() {
		return vmAllocationRequest;
	}
	
	public Vm getVM() {
		return vm;
	}
	
	public void setVMAllocation(VmAllocation vmAllocation) {
		this.vmAllocation = vmAllocation;
	}
	
	public VmAllocation getVMAllocation() {
		return vmAllocation;
	}
	
	public void preExecute() {
		if (!complete) {
			simulation.getTraceLogger().info("#ms," + source.getId() + "," + target.getId() + "," + vm.getId());
		}
	}
	
	public void postExecute() {
		if (complete) {
			simulation.getTraceLogger().info("#mc," + source.getId() + "," + target.getId() + "," + vm.getId());
		}
	}

}
