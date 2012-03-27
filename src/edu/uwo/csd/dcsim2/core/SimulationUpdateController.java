package edu.uwo.csd.dcsim2.core;

public interface SimulationUpdateController {

	public void beginSimulation();
	public void updateSimulation(long simulationTime);
	public void completeSimulation(long simulationTime);
	
}
