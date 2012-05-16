package edu.uwo.csd.dcsim.management;

import edu.uwo.csd.dcsim.*;
import edu.uwo.csd.dcsim.core.*;

public abstract class VMRelocationPolicy extends ManagementPolicy {
	
	DataCentre dc;
	long interval;
		
	public VMRelocationPolicy(Simulation simulation, DataCentre dc, long interval, long firstEvent) {
		super(simulation, firstEvent);
		this.dc = dc;
		this.interval = interval;
	}

	@Override
	public long getNextExecutionTime() {		
		return simulation.getSimulationTime() + interval;
	}

	@Override
	public void processEvent(Event e) {
		//nothing to do
	}
	
}
