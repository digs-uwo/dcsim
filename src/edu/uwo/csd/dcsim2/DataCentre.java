package edu.uwo.csd.dcsim2;

import java.util.ArrayList;
import java.util.LinkedList;

import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.host.*;

public class DataCentre extends SimulationEntity {

	private LinkedList<Host> hosts;
	
	public DataCentre() {
		hosts = new LinkedList<Host>();
	}
	
	public void addHost(Host host) {
		hosts.add(host);
	}
	
	public void addHosts(ArrayList<Host> hosts) {
		hosts.addAll(hosts);
	}
	
	@Override
	public void handleEvent(Event e) {
		// TODO Auto-generated method stub
		
	}

}
