package edu.uwo.csd.dcsim2.management;

import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.*;

public abstract class VMRelocationPolicy extends ManagementPolicy {

	protected static int migrationCount = 0;
	
	DataCentre dc;
	long interval;
	
	public static int getMigrationCount() {
		return migrationCount;
	}
	
	public VMRelocationPolicy(DataCentre dc, long interval) {
		this.dc = dc;
		this.interval = interval;
	}

	@Override
	public long getNextExecutionTime() {		
		return Simulation.getInstance().getSimulationTime() + interval;
	}

	@Override
	public void processEvent(Event e) {
		//nothing to do
	}
	
}
