package edu.uwo.csd.dcsim.application.workload;

import edu.uwo.csd.dcsim.core.Simulation;

/**
 * TwoLevelWorkload keeps a constant workload level until a specified time at which point it switches to a second constant level.
 * 
 * @author Michael Tighe
 *
 */
public class TwoLevelWorkload extends Workload {

	int firstLevel = 0;
	int secondLevel = 0;
	long switchTime;
	int workLevel;
	
	public TwoLevelWorkload(Simulation simulation, int firstLevel, int secondLevel, long switchTime) {
		super(simulation);
		
		this.firstLevel = firstLevel;
		this.secondLevel = secondLevel;
		this.switchTime = switchTime;
		workLevel = firstLevel;
	}
	
	public TwoLevelWorkload(Simulation simulation, long switchTime) {
		super(simulation);
		
		this.switchTime = switchTime;
		workLevel = firstLevel;
	}
	
	public void setFirstLevel(int firstLevel) {
		this.firstLevel = firstLevel;
	}
	
	public int getFirstLevel() {
		return firstLevel;
	}
	
	public void setSecondLevel(int secondLevel) {
		this.secondLevel = secondLevel;
	}
	
	public int getSecondLevel() {
		return secondLevel;
	}
	
	@Override
	protected int getCurrentWorkLevel() {
		return workLevel;
	}

	@Override
	protected long updateWorkLevel() {
		if (simulation.getSimulationTime() == switchTime) {
			workLevel = secondLevel;
			return 0;
		} else {
			return switchTime;
		}
	}
	
	

}
