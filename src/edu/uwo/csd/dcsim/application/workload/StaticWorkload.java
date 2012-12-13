package edu.uwo.csd.dcsim.application.workload;

import edu.uwo.csd.dcsim.core.Simulation;

/**
 * StaticWorkload generates a constant workload level through the entire simulation
 * @author Michael Tighe
 *
 */
public class StaticWorkload extends Workload {

	double workPerSecond;
	
	public StaticWorkload(Simulation simulation, double workPerSecond) {
		super(simulation);
		
		this.workPerSecond = workPerSecond;
	}

	@Override
	protected double getCurrentWorkLevel() {
		return workPerSecond;
	}

	@Override
	protected long updateWorkLevel() {
		//do nothing, static workload never changes
		return 0;
	}

}
