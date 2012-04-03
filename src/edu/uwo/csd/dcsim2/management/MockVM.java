package edu.uwo.csd.dcsim2.management;

import edu.uwo.csd.dcsim2.vm.*;

public class MockVM {

	private double cpuInUse;
	private double cpuAlloc;
	private VM vm;
	
	public MockVM(VM vm) {
		this.vm = vm;
		cpuInUse = vm.getResourcesInUse().getCpu();
		cpuAlloc = vm.getVMAllocation().getCpu();
	}
	
	public double getCpuInUse() {
		return cpuInUse;
	}
	
	public double getCpuAlloc() {
		return cpuAlloc;
	}
	
	public VM getVM() {
		return vm;
	}
	
}
