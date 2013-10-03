package edu.uwo.csd.dcsim.management.capabilities;

import java.util.*;

import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.management.*;

public class HostPoolManager extends ManagerCapability {
	
	protected Map<Integer, HostData> hostMap = new HashMap<Integer, HostData>();

	public void addHost(Host host, AutonomicManager hostManager) {
		hostMap.put(host.getId(), new HostData(host, hostManager));
	}
	
	public Collection<HostData> getHosts() {
		return hostMap.values();
	}
	
	public HostData getHost(int id) {
		return hostMap.get(id);
	}
	
}
