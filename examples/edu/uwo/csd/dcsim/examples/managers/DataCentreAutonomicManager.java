package edu.uwo.csd.dcsim.examples.managers;

import java.util.*;

import edu.uwo.csd.dcsim.DataCentre;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.management.*;

public class DataCentreAutonomicManager extends AutonomicManager {

	DataCentre dc;
	private Map<Integer, Host> hostMap;	 //store hosts in a Map, as most uses will be to lookup individual hosts
	private Map<Integer, ArrayList<HostStatus>> hostStatus = new HashMap<Integer, ArrayList<HostStatus>>();
	
	public DataCentreAutonomicManager(DataCentre dc) {
		this.dc = dc;
	}
	
	public Collection<Host> getHosts() {
		return hostMap.values();
	}
	
	public Host getHost(int id) {
		
		if (hostMap == null){
			this.hostMap = new HashMap<Integer, Host>();
			
			for (Host host : dc.getHosts()) {
				hostMap.put(host.getId(), host);
			}
		}
		
		if (!hostMap.containsKey(id)) {
			System.out.println("!!!!!!!!!!!!!!! " + id);
		}
		return hostMap.get(id);
	}
	
	public Map<Integer, ArrayList<HostStatus>> getHostStatus() {
		return hostStatus;
	}
	
	public ArrayList<HostStatus> getHostStatus(int hostId) {
		return hostStatus.get(hostId);
	}
	
}
