package edu.uwo.csd.dcsim;

import java.util.ArrayList;
import java.util.Collection;

import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.host.*;

/**
 * Represents a single simulated DataCentre, which consists of a set of Host machines.
 * 
 * Represents a single simulated data centre, which consists of a set of 
 * Clusters, connected through two central switches (data and management 
 * networks, respectively).
 * 
 * @author Michael Tighe
 *
 */
public class DataCentre implements SimulationEventListener {
	
	private ArrayList<Host> hosts; //the hosts in this datacentre
	Simulation simulation;
	
	private ArrayList<Cluster> clusters = null;				// Clusters in this data centre.
	
	private Switch dataNetworkSwitch = null;				// Data network switch.
	private Switch mgmtNetworkSwitch = null;				// Management network switch.
	
	/**
	 * Creates a new DataCentre, specifying the VMPlacementPolicy that will be used
	 * to place VMs in the DataCentre
	 * @param vmPlacementPolicy
	 */
	public DataCentre(Simulation simulation) {
		hosts = new ArrayList<Host>();
		this.simulation = simulation;
	}
	
	/**
	 * Creates an instance of DataCentre.
	 */
	public DataCentre(Simulation simulation, SwitchFactory switchFactory) {
		this.simulation = simulation;
		this.dataNetworkSwitch = switchFactory.newInstance();
		this.mgmtNetworkSwitch = switchFactory.newInstance();
		this.clusters = new ArrayList<Cluster>();
		hosts = new ArrayList<Host>();
	}
	
	/**
	 * Add a Host to the DataCentre
	 * @param host
	 */
	public void addHost(Host host) {
		hosts.add(host);
	}
	
	/**
	 * Add a Collection of Hosts to the DataCentre
	 * @param newHosts
	 */
	public void addHosts(Collection<Host> newHosts) {
		hosts.addAll(newHosts);
	}
	
	/**
	 * Get the collection of Hosts in this DataCentre
	 * @return
	 */
	public ArrayList<Host> getHosts() {
		return hosts;
	}
	
	@Override
	public void handleEvent(Event e) {
		//at present, there are no events received by DataCentre. This is left as a hook in case of future need
	}
	
	/**
	 * Log state of the DataCentre
	 */
	public void logState() {
		for (Host host : hosts) {
			host.logState();
		}
	}
	
	public double getCurrentPowerConsumption() {
		double power = 0;
		for (Host host : hosts)
			power += host.getCurrentPowerConsumption();
		return power;
	}
	
	/**
	 * Adds a cluster to the data centre, connecting it to both data and management networks.
	 * 
	 * It also adds the Hosts in the Cluster to the DataCentre (required for the Simulation class 
	 * to work properly).
	 */
	public void addCluster(Cluster cluster) {
		// Set Data Network.
		Switch clusterSwitch = cluster.getMainDataSwitch();
		Link link = new Link(clusterSwitch, dataNetworkSwitch);
		clusterSwitch.setUpLink(link);
		dataNetworkSwitch.addPort(link);
		
		// Set Management Network.
		clusterSwitch = cluster.getMainMgmtSwitch();
		link = new Link(clusterSwitch, mgmtNetworkSwitch);
		clusterSwitch.setUpLink(link);
		mgmtNetworkSwitch.addPort(link);
		
		clusters.add(cluster);
		
		for (Rack rack : cluster.getRacks()) {
			hosts.addAll(rack.getHosts());
		}
	}
	
	public ArrayList<Cluster> getClusters() { return clusters; }
	
	public Switch getDataNetworkSwitch() { return dataNetworkSwitch; }
	
	public Switch getMgmtNetworkSwitch() { return mgmtNetworkSwitch; }
	
}
