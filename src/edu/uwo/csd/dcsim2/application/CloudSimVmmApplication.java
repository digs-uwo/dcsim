package edu.uwo.csd.dcsim2.application;

import edu.uwo.csd.dcsim2.vm.VM;

public class CloudSimVmmApplication extends VmmApplication {

	public CloudSimVmmApplication() {
		cpuOverhead = 0;
		bandwidthOverhead = 0;
	}
	
	@Override
	protected double getMigrationCpu(VM migratingVm) {
		return 0;
	}
	
	@Override
	protected double getMigrationBandwidth(VM migratingVm) {
		return 0;
	}
	
}
