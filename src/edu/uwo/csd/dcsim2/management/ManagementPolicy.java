package edu.uwo.csd.dcsim2.management;

import edu.uwo.csd.dcsim2.core.*;

public abstract class ManagementPolicy extends SimulationEntity {

	public static final int MANAGEMENT_POLICY_EXECUTE_EVENT = 1;
		
	public ManagementPolicy() {
		//schedule initial update event
		Simulation.getInstance().sendEvent(new Event(ManagementPolicy.MANAGEMENT_POLICY_EXECUTE_EVENT, 0, this, this));
	}
	
	public abstract void execute();
	public abstract long getNextExecutionTime();
	public abstract void processEvent(Event e);
	
	@Override
	public void handleEvent(Event e) {
		if (e.getType() == MANAGEMENT_POLICY_EXECUTE_EVENT) {
			execute();
			Simulation.getInstance().sendEvent(new Event(ManagementPolicy.MANAGEMENT_POLICY_EXECUTE_EVENT, getNextExecutionTime(), this, this));
		} else {
			processEvent(e);
		}
	}

	
}
