package edu.uwo.csd.dcsim2;

import java.util.ArrayList;

import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.host.*;
import edu.uwo.csd.dcsim2.management.*;

public class DataCentre extends SimulationEntity {

	private ArrayList<Host> hosts;
	VMPlacementPolicy vmPlacementPolicy;
	
	public DataCentre(VMPlacementPolicy vmPlacementPolicy) {
		hosts = new ArrayList<Host>();
		
		this.vmPlacementPolicy = vmPlacementPolicy;
		vmPlacementPolicy.setDataCentre(this);
	}
	
	public void addHost(Host host) {
		hosts.add(host);
	}
	
	public void addHosts(ArrayList<Host> newHosts) {
		hosts.addAll(newHosts);
	}
	
	public ArrayList<Host> getHosts() {
		return hosts;
	}
	
	public VMPlacementPolicy getVMPlacementPolicy() {
		return vmPlacementPolicy;
	}
	
	public void setVMPlacementPolicy(VMPlacementPolicy vmPlacementPolicy) {
		this.vmPlacementPolicy = vmPlacementPolicy;
	}
	
	@Override
	public void handleEvent(Event e) {
		// TODO Auto-generated method stub
		
	}

	public void logInfo() {
		for (Host host : hosts) {
			host.logInfo();
		}
	}
	
}
