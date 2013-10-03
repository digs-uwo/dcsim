package edu.uwo.csd.dcsim.application.workload;

import edu.uwo.csd.dcsim.core.Simulation;

/**
 * StaticWorkload generates a constant workload level through the entire simulation
 * @author Michael Tighe
 *
 */
public class StaticWorkload extends Workload {

	int workLevel;
	
	public StaticWorkload(Simulation simulation) {
		super(simulation);
	}
	
	public StaticWorkload(Simulation simulation, int workLevel) {
		super(simulation);
		
		this.workLevel = workLevel;
	}
	
	public void setWorkLevel(int workLevel) {
		this.workLevel = workLevel;
	}

	@Override
	protected int getCurrentWorkLevel() {
		return workLevel;
	}

	@Override
	protected long updateWorkLevel() {
		//do nothing, static workload never changes
		return 0;
	}

}
