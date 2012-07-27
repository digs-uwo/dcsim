package edu.uwo.csd.dcsim;

import java.util.ArrayList;
import java.util.Collection;

import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.core.metrics.DCCpuUtilMetric;
import edu.uwo.csd.dcsim.host.*;
import edu.uwo.csd.dcsim.management.*;

/**
 * Represents a single simulated DataCentre, which consists of a set of Host machines.
 * 
 * @author Michael Tighe
 *
 */
public class DataCentre implements SimulationEventListener {
	
	public static final String DC_UTIL_METRIC = "avgDcUtil";

	private ArrayList<Host> hosts; //the hosts in this datacentre
	VMPlacementPolicy vmPlacementPolicy; //the placement policy for this datacentre
	Simulation simulation;
	
	/**
	 * Creates a new DataCentre, specifying the VMPlacementPolicy that will be used
	 * to place VMs in the DataCentre
	 * @param vmPlacementPolicy
	 */
	public DataCentre(Simulation simulation, VMPlacementPolicy vmPlacementPolicy) {
		hosts = new ArrayList<Host>();
		this.simulation = simulation;
		
		this.vmPlacementPolicy = vmPlacementPolicy;
		vmPlacementPolicy.setDataCentre(this);
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
	
	/**
	 * Get the VMPlacementPolicy in use by this DataCentre to place new VMs
	 * @return
	 */
	public VMPlacementPolicy getVMPlacementPolicy() {
		return vmPlacementPolicy;
	}
	
	/**
	 * Set the VMPlacementPolicy to be used by this DataCentre to place new VMs
	 * onto Hosts
	 * @param vmPlacementPolicy
	 */
	public void setVMPlacementPolicy(VMPlacementPolicy vmPlacementPolicy) {
		this.vmPlacementPolicy = vmPlacementPolicy;
	}
	
	@Override
	public void handleEvent(Event e) {
		//at present, there are no events received by DataCentre. This is left as a hook in case of future need
	}

	/**
	 * Update metrics regarding the DataCentre
	 */
	public void updateMetrics() {
		
		DCCpuUtilMetric dcUtilMetric = DCCpuUtilMetric.getMetric(simulation, DC_UTIL_METRIC);
		
		for (Host host : hosts) {
			host.updateMetrics();
			
			dcUtilMetric.addHostUse(host.getCpuManager().getCpuInUse(), host.getTotalCpu());
		}
	}
	
	/**
	 * Log information about the DataCentre
	 */
	public void logInfo() {
		for (Host host : hosts) {
			host.logInfo();
		}
	}
	
	public double getCurrentPowerConsumption() {
		double power = 0;
		for (Host host : hosts)
			power += host.getCurrentPowerConsumption();
		return power;
	}
	
}
