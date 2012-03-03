package edu.uwo.csd.dcsim2.application;

import edu.uwo.csd.dcsim2.core.Simulation;

public class TwoLevelWorkload extends Workload {

	double firstLevel;
	double secondLevel;
	long switchTime;
	double workPerSecond;
	
	public TwoLevelWorkload(double firstLevel, double secondLevel, long switchTime) {
		super();
		
		this.firstLevel = firstLevel;
		this.secondLevel = secondLevel;
		this.switchTime = switchTime;
		workPerSecond = firstLevel;
	}
	
	@Override
	protected double retrievePendingWork() {
		return workPerSecond * ((Simulation.getSimulation().getSimulationTime() - Simulation.getSimulation().getLastUpdate()) / 1000.0);
	}

	@Override
	protected long updateWorkLevel() {
		if (Simulation.getSimulation().getSimulationTime() == switchTime) {
			workPerSecond = secondLevel;
			return 0;
		} else {
			return switchTime;
		}
	}
	
	

}
