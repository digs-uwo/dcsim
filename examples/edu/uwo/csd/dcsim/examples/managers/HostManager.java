package edu.uwo.csd.dcsim.examples.managers;

import edu.uwo.csd.dcsim.host.*;

public class HostManager {

	private Host host;
	
	public HostManager(Host host) {
		this.host = host;
	}
	
	public Host getHost() {
		return host;
	}
	
}
