package edu.uwo.csd.dcsim2.management;

import edu.uwo.csd.dcsim2.DataCentre;
import edu.uwo.csd.dcsim2.core.Event;
import edu.uwo.csd.dcsim2.core.Simulation;

public abstract class VMConsolidationPolicy extends ManagementPolicy {

	DataCentre dc;
	long interval;
	
	public VMConsolidationPolicy(DataCentre dc, long interval) {
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
