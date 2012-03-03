package edu.uwo.csd.dcsim2.core;

import java.util.ArrayList;

public abstract class SimulationEntity {

	private static ArrayList<SimulationEntity> simulationEntities =  new ArrayList<SimulationEntity>();
	
	public static ArrayList<SimulationEntity> getSimulationEntities() {
		return simulationEntities;
	}
	
	/**
	 * Preset the size of the entity list. If the number of SimulationEntities to be created is known a priori,
	 * preseting the size (before creating any SimulationEntity objects) will optimize registerEntity() performance.
	 * @param count
	 */
	public static void presetEntityCount(int count) {
		simulationEntities = new ArrayList<SimulationEntity>(count);
	}
	
	public SimulationEntity() {
		simulationEntities.add(this);
	}
	
	public abstract void handleEvent(Event e);

	
}
