package edu.uwo.csd.dcsim2.management;

import java.util.ArrayList;

import edu.uwo.csd.dcsim2.*;
import edu.uwo.csd.dcsim2.vm.*;
import edu.uwo.csd.dcsim2.host.*;
import edu.uwo.csd.dcsim2.core.*;

public abstract class VMPlacementPolicy extends SimulationEntity {
	
	Simulation simulation;
	DataCentre datacentre;
	
	public VMPlacementPolicy(Simulation simulation) {
		this.simulation = simulation;
	}
	
	public void setDataCentre(DataCentre datacentre) {
		this.datacentre = datacentre;
	}
	
	public DataCentre getDataCentre() {
		return datacentre;
	}
	
	
	public abstract boolean submitVM(VMAllocationRequest vmAllocationRequest);
	public abstract boolean submitVMs(ArrayList<VMAllocationRequest> vmAllocationRequests);	
	
	@Override
	public void handleEvent(Event e) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean submitVM(VMAllocationRequest vmAllocationRequest, Host host) {

		if (host.hasCapacity(vmAllocationRequest)) {
			sendVM(vmAllocationRequest, host);
			return true;
		} else {
			return false;
		}
		
	}
	
	protected void sendVM(VMAllocationRequest vmAllocationRequest, Host host) {
		
		if (host.getState() != Host.HostState.ON && host.getState() != Host.HostState.POWERING_ON) {
			simulation.sendEvent(
					new Event(Host.HOST_POWER_ON_EVENT,
							simulation.getSimulationTime(),
							this,
							host)
					);
		}
		
		host.submitVM(vmAllocationRequest);
	}

}
