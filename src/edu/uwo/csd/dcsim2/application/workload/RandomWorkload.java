package edu.uwo.csd.dcsim2.application.workload;

import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.core.Utility;

public class RandomWorkload extends Workload {

	long stepSize;
	double scaleFactor;
	
	public RandomWorkload(double scaleFactor, long stepSize) {
		super();	
		
		this.stepSize = 0;
		this.scaleFactor = scaleFactor;
	}
	
	@Override
	protected double retrievePendingWork() {
		return (int)Math.round(Utility.getRandom().nextDouble() * scaleFactor);
	}

	@Override
	protected long updateWorkLevel() {
		return Simulation.getInstance().getSimulationTime() + stepSize;
	}
		
	
}
