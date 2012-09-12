package edu.uwo.csd.dcsim.management;

import java.util.ArrayList;

import edu.uwo.csd.dcsim.*;
import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.host.*;
import edu.uwo.csd.dcsim.vm.*;

public abstract class VMPlacementPolicy implements SimulationEventListener {
	
	Simulation simulation;
	protected DataCentre datacentre;
	
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
		
		//if the host is not ON or POWERING_ON, then send an event to power on the host
		if (host.getState() != Host.HostState.ON && host.getState() != Host.HostState.POWERING_ON) {
			simulation.sendEvent(
					new Event(Host.HOST_POWER_ON_EVENT,
							simulation.getSimulationTime(),
							this,
							host)
					);
		}
		
		//if the host is set to shutdown upon completion of outgoing migrations, cancel this shutdown
		if (host.isShutdownPending()) {
			/*
			 * note that we directly make this call rather than send an event, as we want to
			 * avoid the host beginning its shutdown process before receiving the cancel event 
			 */
			host.cancelPendingShutdown(); 
		}
		
		host.submitVM(vmAllocationRequest);
	}

}
