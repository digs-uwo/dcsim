package edu.uwo.csd.dcsim.management;

import edu.uwo.csd.dcsim.DataCentre;
import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.Simulation;

public abstract class VMConsolidationPolicy extends ManagementPolicy {

	DataCentre dc;
	long interval;
	
	public VMConsolidationPolicy(Simulation simulation, DataCentre dc, long interval, long firstEvent) {
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
