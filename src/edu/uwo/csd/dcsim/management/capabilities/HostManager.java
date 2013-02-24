package edu.uwo.csd.dcsim.management.capabilities;

import edu.uwo.csd.dcsim.host.*;

public class HostManager extends HostCapability {

	private Host host;
	
	public HostManager(Host host) {
		this.host = host;
	}
	
	public Host getHost() {
		return host;
	}
	
}
