package edu.uwo.csd.dcsim.application.workload;

import edu.uwo.csd.dcsim.core.Simulation;

/**
 * TwoLevelWorkload keeps a constant workload level until a specified time at which point it switches to a second constant level.
 * 
 * @author Michael Tighe
 *
 */
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
	protected double getCurrentWorkLevel() {
		return workPerSecond;
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
