package edu.uwo.csd.dcsim.application.workload;

import edu.uwo.csd.dcsim.core.Simulation;

public class StaticWorkload extends Workload {

	double workPerSecond;
	
	public StaticWorkload(Simulation simulation, double workPerSecond) {
		super(simulation);
		
		this.workPerSecond = workPerSecond;
	}

	@Override
	protected double retrievePendingWork() {
		return workPerSecond * ((simulation.getElapsedTime()) / 1000.0);
	}

	@Override
	protected long updateWorkLevel() {
		//do nothing, static workload never changes
		return 0;
	}

}
