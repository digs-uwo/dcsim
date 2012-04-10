package edu.uwo.csd.dcsim2.management;

import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.*;

public abstract class VMRelocationPolicy extends ManagementPolicy {
	
	DataCentre dc;
	long interval;
		
	public VMRelocationPolicy(DataCentre dc, long interval, long firstEvent) {
		super(firstEvent);
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
