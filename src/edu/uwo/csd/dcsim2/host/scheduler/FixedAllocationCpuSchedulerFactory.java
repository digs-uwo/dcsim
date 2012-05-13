package edu.uwo.csd.dcsim2.host.scheduler;

import edu.uwo.csd.dcsim2.common.ObjectFactory;
import edu.uwo.csd.dcsim2.core.Simulation;

/**
 * Constructs instances of FixedAllocationCpuScheduler
 * 
 * @author Michael Tighe
 *
 */
public class FixedAllocationCpuSchedulerFactory implements ObjectFactory<FixedAllocationCpuScheduler> {

	private final Simulation simulation;
	
	public FixedAllocationCpuSchedulerFactory(Simulation simulation) {
		this.simulation = simulation;
	}
	
	@Override
	public FixedAllocationCpuScheduler newInstance() {
		return new FixedAllocationCpuScheduler(simulation);
	}

}
