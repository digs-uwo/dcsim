package edu.uwo.csd.dcsim2.management.action;

import edu.uwo.csd.dcsim2.core.*;

public interface ManagementAction {

	public void execute(Simulation simulation, SimulationEventListener triggeringEntity);
	
}
