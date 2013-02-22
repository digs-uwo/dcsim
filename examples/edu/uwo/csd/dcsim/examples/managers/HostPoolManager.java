package edu.uwo.csd.dcsim.examples.managers;

import java.util.*;

import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.management.*;

public class HostPoolManager {

	private Map<Integer, Host> hostMap = new HashMap<Integer, Host>();	 //store hosts in a Map, as most uses will be to lookup individual hosts
	private Map<Integer, AutonomicManager> hostManagers = new HashMap<Integer, AutonomicManager>();
	private Map<Integer, ArrayList<HostStatus>> hostStatus = new HashMap<Integer, ArrayList<HostStatus>>();
	
	public void addHostManager(int hostId, AutonomicManager hostManager) {
		hostManagers.put(hostId, hostManager);
	}

	public void addHosts(List<Host> hosts) {
		for (Host host : hosts) {
			addHost(host);
		}
	}
	
	public void addHost(Host host) {
		hostMap.put(host.getId(), host);
	}
	
	public Collection<Host> getHosts() {
		return hostMap.values();
	}
	
	public Host getHost(int id) {
		return hostMap.get(id);
	}
	
	public Map<Integer, ArrayList<HostStatus>> getHostStatus() {
		return hostStatus;
	}
	
	public ArrayList<HostStatus> getHostStatus(int hostId) {
		return hostStatus.get(hostId);
	}
	
	public AutonomicManager getHostManager(int hostId) {
		return hostManagers.get(hostId);
	}
	
}
