package edu.uwo.csd.dcsim2.application;

import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.vm.VM;

public class CloudSimVmmApplication extends VmmApplication {

	public CloudSimVmmApplication(Simulation simulation) {
		super(simulation);
		
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
