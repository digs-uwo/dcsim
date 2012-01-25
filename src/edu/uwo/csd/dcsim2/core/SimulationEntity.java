package edu.uwo.csd.dcsim2.core;

import java.util.ArrayList;

public abstract class SimulationEntity {

	private long lastUpdate;
	private ArrayList<SimulationEntity> dependencies;
	
	public SimulationEntity() {
		lastUpdate = 0;
		dependencies = new ArrayList<SimulationEntity>();
		Simulation.getSimulation().registerEntity(this);
	}
	
	public abstract void handleEvent(Event e);
	
	public long getLastUpdate() {
		return lastUpdate;
	}
	
	protected ArrayList<SimulationEntity> getDependencies() {
		return dependencies;
	}
	
	/**
	 * Update entity processing to bring it up to current time.
	 */
	public void updateEntity() {
		if (Simulation.getSimulation().getSimulationTime() > lastUpdate) { //don't update the same entity twice
			
			//ensure dependencies are updated first
			for (SimulationEntity d : dependencies) {
				d.updateEntity();
			}
			
			update();
			
			lastUpdate = Simulation.getSimulation().getSimulationTime();
		}
	}
	
	protected abstract void update();
	
}
