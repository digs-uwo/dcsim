package edu.uwo.csd.dcsim.application.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.SimulationEventListener;

public class SpawnApplicationEvent extends Event {

	int currentRate;
	
	public SpawnApplicationEvent(SimulationEventListener serviceProducer, int currentRate) {
		super(serviceProducer);
		
		this.currentRate = currentRate;
	}
	
	public int getCurrentRate() {
		return currentRate;
	}

}
