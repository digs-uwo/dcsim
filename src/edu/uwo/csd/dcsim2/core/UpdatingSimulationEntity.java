package edu.uwo.csd.dcsim2.core;

import java.util.Vector;

public abstract class UpdatingSimulationEntity extends SimulationEntity {

	private long lastUpdate;
	private Vector<UpdatingSimulationEntity> dependencies;
	
	public UpdatingSimulationEntity() {
		super();
		lastUpdate = 0;
		dependencies = new Vector<UpdatingSimulationEntity>();
	}
	
	public long getLastUpdate() {
		return lastUpdate;
	}
	
	protected Vector<UpdatingSimulationEntity> getDependencies() {
		return dependencies;
	}
	
	
	/**
	 * Update entity processing to bring it up to current time.
	 */
	public void updateEntity() {
		if (Simulation.getInstance().getSimulationTime() > lastUpdate) { //don't update the same entity twice
			
			//ensure dependencies are updated first
			for (UpdatingSimulationEntity d : dependencies) {
				d.updateEntity();
			}
			
			update();
			
			lastUpdate = Simulation.getInstance().getSimulationTime();
		}
	}
	
	protected abstract void update();
	
}
