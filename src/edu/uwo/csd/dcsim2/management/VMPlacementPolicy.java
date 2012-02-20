package edu.uwo.csd.dcsim2.management;

import java.util.ArrayList;

import edu.uwo.csd.dcsim2.*;
import edu.uwo.csd.dcsim2.vm.*;
import edu.uwo.csd.dcsim2.host.*;
import edu.uwo.csd.dcsim2.core.*;

public abstract class VMPlacementPolicy extends SimulationEntity {
	
	DataCentre datacentre;
	
	public VMPlacementPolicy() {
		
	}
	
	public void setDataCentre(DataCentre datacentre) {
		this.datacentre = datacentre;
	}
	
	public DataCentre getDataCentre() {
		return datacentre;
	}
	
	
	public abstract boolean submitVM(VMAllocationRequest vmAllocationRequest);
	public abstract boolean submitVMs(ArrayList<VMAllocationRequest> vmAllocationRequests);	
	public abstract boolean submitVM(VMAllocationRequest vmAllocationRequest, Host host);
	
	@Override
	public void handleEvent(Event e) {
		// TODO Auto-generated method stub
		
	}

}
