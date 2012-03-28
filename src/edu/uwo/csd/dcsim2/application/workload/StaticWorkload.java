package edu.uwo.csd.dcsim2.application.workload;

import edu.uwo.csd.dcsim2.core.Simulation;

public class StaticWorkload extends Workload {

	double workPerSecond;
	
	public StaticWorkload(double workPerSecond) {
		super();
		
		this.workPerSecond = workPerSecond;
	}

	@Override
	protected double retrievePendingWork() {
		return workPerSecond * ((Simulation.getInstance().getSimulationTime() - Simulation.getInstance().getLastUpdate()) / 1000.0);
	}

	@Override
	protected long updateWorkLevel() {
		//do nothing, static workload never changes
		return 0;
	}

}
