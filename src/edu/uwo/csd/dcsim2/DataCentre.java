package edu.uwo.csd.dcsim2;

import java.util.ArrayList;

import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.host.*;

public class DataCentre extends SimulationEntity {

	private ArrayList<Host> hosts;
	
	public DataCentre() {
		hosts = new ArrayList<Host>();
	}
	
	public void addHost(Host host) {
		hosts.add(host);
	}
	
	public void addHosts(ArrayList<Host> hosts) {
		hosts.addAll(hosts);
	}
	
	public ArrayList<Host> getHosts() {
		return hosts;
	}
	
	@Override
	public void handleEvent(Event e) {
		// TODO Auto-generated method stub
		
	}

}
