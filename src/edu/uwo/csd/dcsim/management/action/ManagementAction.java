package edu.uwo.csd.dcsim.management.action;

import edu.uwo.csd.dcsim.core.*;

public interface ManagementAction {

	public void execute(Simulation simulation, Object triggeringEntity);
	
}
