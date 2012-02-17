package edu.uwo.csd.dcsim2;

import java.util.LinkedList;

import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.host.*;

public class Datacentre extends SimulationEntity {

	private LinkedList<Host> hosts;
	
	public Datacentre() {
		hosts = new LinkedList<Host>();
	}
	
	public void addHost(Host host) {
		hosts.add(host);
	}
	
	@Override
	public void handleEvent(Event e) {
		// TODO Auto-generated method stub
		
	}

}
