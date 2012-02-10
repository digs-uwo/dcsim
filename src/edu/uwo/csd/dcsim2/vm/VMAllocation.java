package edu.uwo.csd.dcsim2.vm;

import java.util.Vector;

import edu.uwo.csd.dcsim2.host.*;

public class VMAllocation {

	VM vm;
	VMDescription vmDescription;
	Host host;

	
	public VMAllocation(VMDescription vmDescription, Host host) {
		this.vmDescription = vmDescription;
		this.host = host;
		vm = null;
	}
	
	public void setVm(VM vm) {
		this.vm = vm;
	}
	
	public VM getVm() {
		return vm;
	}
	
	public Host getHost() {
		return host;
	}
	
	public VMDescription getVMDescription() {
		return vmDescription;
	}
	
}
