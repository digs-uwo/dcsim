package edu.uwo.csd.dcsim2.application.workload;

import edu.uwo.csd.dcsim2.core.Simulation;

public class RandomWorkload extends Workload {

	long stepSize;
	double scaleFactor;
	int workLevel = 0;
	
	public RandomWorkload(Simulation simulation, double scaleFactor, long stepSize) {
		super(simulation);	
		
		this.stepSize = stepSize;
		this.scaleFactor = scaleFactor;
		
		workLevel = generateRandomWorkLevel();
	}
	
	protected int generateRandomWorkLevel() {
		return (int)Math.round(simulation.getRandom().nextDouble() * scaleFactor);
	}
	
	@Override
	protected double retrievePendingWork() {
		return workLevel * simulation.getElapsedSeconds();
	}

	@Override
	protected long updateWorkLevel() {
		workLevel = generateRandomWorkLevel();
		return simulation.getSimulationTime() + stepSize;
	}
		
	
}
