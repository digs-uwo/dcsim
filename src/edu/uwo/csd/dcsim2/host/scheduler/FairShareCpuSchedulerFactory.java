package edu.uwo.csd.dcsim2.host.scheduler;

import edu.uwo.csd.dcsim2.common.ObjectFactory;
import edu.uwo.csd.dcsim2.core.Simulation;

/**
 * Constructs instances of FairShareCpuScheduler
 * 
 * @author Michael Tighe
 *
 */
public class FairShareCpuSchedulerFactory implements ObjectFactory<FairShareCpuScheduler> {

	private final Simulation simulation;
	
	public FairShareCpuSchedulerFactory(Simulation simulation) {
		this.simulation = simulation;
	}
	
	@Override
	public FairShareCpuScheduler newInstance() {
		return new FairShareCpuScheduler(simulation);
	}

}
