package edu.uwo.csd.dcsim2.core;

public abstract class SimulationEntity {

	public SimulationEntity() {
		Simulation.getSimulation().registerEntity(this);
	}
	
	public abstract void handleEvent(Event e);

	
}
