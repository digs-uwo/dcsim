package edu.uwo.csd.dcsim2.application.workload;

import edu.uwo.csd.dcsim2.core.Simulation;

public class TwoLevelWorkload extends Workload {

	double firstLevel;
	double secondLevel;
	long switchTime;
	double workPerSecond;
	
	public TwoLevelWorkload(Simulation simulation, double firstLevel, double secondLevel, long switchTime) {
		super(simulation);
		
		this.firstLevel = firstLevel;
		this.secondLevel = secondLevel;
		this.switchTime = switchTime;
		workPerSecond = firstLevel;
	}
	
	@Override
	protected double retrievePendingWork() {
		return workPerSecond * ((simulation.getSimulationTime() - simulation.getLastUpdate()) / 1000.0);
	}

	@Override
	protected long updateWorkLevel() {
		if (simulation.getSimulationTime() == switchTime) {
			workPerSecond = secondLevel;
			return 0;
		} else {
			return switchTime;
		}
	}
	
	

}
