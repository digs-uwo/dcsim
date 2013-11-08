package edu.uwo.csd.dcsim.host;

import java.util.ArrayList;

import edu.uwo.csd.dcsim.common.HashCodeUtil;
import edu.uwo.csd.dcsim.common.ObjectBuilder;
import edu.uwo.csd.dcsim.core.*;

/**
 * A cluster within a data centre. Encompasses a set of racks and two sets of
 * switches (data and management networks, respectively).
 * 
 * @author Gaston Keller
 *
 */
public final class Cluster implements SimulationEventListener {
	
	private Simulation simulation;
	
	private int id = 0;
	private int nRacks = 0;								// Number of racks in the cluster.
	private int nSwitches = 0;								// Number of switches in the cluster.
	
	private ArrayList<Rack> racks = null;					// List of racks.
	
	private ArrayList<Switch> dataSwitches = null;			// Data network switches.
	private ArrayList<Switch> mgmtSwitches = null;			// Management network switches.
	
	private Switch mainDataSwitch = null;					// Data network main (top-level) switch.
	private Switch mainMgmtSwitch = null;					// Management network main (top-level) switch.
	
	public enum ClusterState {ON, SUSPENDED, OFF;}
	private ClusterState state;
	
	private final int hashCode;
	
	private Cluster(Builder builder) {
		
		this.simulation = builder.simulation;
		
		this.id = simulation.nextId(Cluster.class.toString());
		
		this.nRacks = builder.nRacks;
		this.nSwitches = builder.nSwitches;
		
		this.dataSwitches = new ArrayList<Switch>(nSwitches);
		this.mgmtSwitches = new ArrayList<Switch>(nSwitches);
		for (int i = 0; i < nSwitches; i++) {
			this.dataSwitches.add(builder.switchFactory.newInstance());
			this.mgmtSwitches.add(builder.switchFactory.newInstance());
		}
		
		this.racks = new ArrayList<Rack>(nRacks);
		for (int i = 0; i < nRacks; i++) {
			Rack rack = builder.rackBuilder.build();
			rack.setCluster(this);
			
			// Set Data Network.
			Switch rackSwitch = rack.getDataNetworkSwitch();
			Link link = new Link(rackSwitch, dataSwitches.get(i % nSwitches));
			rackSwitch.setUpLink(link);
			dataSwitches.get(i % nSwitches).addPort(link);
			
			// Set Management Network.
			rackSwitch = rack.getMgmtNetworkSwitch();
			link = new Link(rackSwitch, mgmtSwitches.get(i % nSwitches));
			rackSwitch.setUpLink(link);
			mgmtSwitches.get(i % nSwitches).addPort(link);
			
			this.racks.add(rack);
		}
		
		// Complete network(s) layout.
		// If there is only one switch, identify it as main (top-level) for 
		// the network.
		// Otherwise, add a central, higher-level switch and connect all other 
		// switches to it -- star topology.
		
		if (nSwitches == 1) {
			mainDataSwitch = dataSwitches.get(0);
			mainMgmtSwitch = mgmtSwitches.get(0);
		}
		else {
			mainDataSwitch = builder.switchFactory.newInstance();
			mainMgmtSwitch = builder.switchFactory.newInstance();
			
			for (int i = 0; i < nSwitches; i++) {
				// Set Data Network.
				Switch aSwitch = dataSwitches.get(i);
				Link link = new Link(aSwitch, mainDataSwitch);
				aSwitch.setUpLink(link);
				mainDataSwitch.addPort(link);
				
				// Set Management Network.
				aSwitch = mgmtSwitches.get(i);
				link = new Link(aSwitch, mainMgmtSwitch);
				aSwitch.setUpLink(link);
				mainMgmtSwitch.addPort(link);
			}
		}
		
		// Set default state.
		state = ClusterState.OFF;
		
		//init hashCode
		hashCode = generateHashCode();
	}
	
	/**
	 * Builds a new Cluster object. This is the only way to instantiate Cluster.
	 * 
	 * @author Gaston Keller
	 *
	 */
	public static class Builder implements ObjectBuilder<Cluster> {

		private final Simulation simulation;
		
		private int nRacks = 0;
		private int nSwitches = 0;
		
		private Rack.Builder rackBuilder = null;
		private SwitchFactory switchFactory = null;
		
		public Builder(Simulation simulation) {
			if (simulation == null)
				throw new NullPointerException();
			this.simulation = simulation;
		}
		
		public Builder nRacks(int val) { this.nRacks = val; return this; }
		
		public Builder nSwitches(int val) { this.nSwitches = val; return this; }
		
		public Builder rackBuilder(Rack.Builder rackBuilder) {
			this.rackBuilder = rackBuilder;
			return this;
		}
		
		public Builder switchFactory(SwitchFactory switchFactory) {
			this.switchFactory = switchFactory;
			return this;
		}
		
		@Override
		public Cluster build() {
			
			if (nRacks == 0 || nSwitches == 0)
				throw new IllegalStateException("Must specify number of racks and switches before building Cluster.");
			if (rackBuilder == null)
				throw new IllegalStateException("Must specify Rack builder before building Cluster.");
			if (switchFactory == null)
				throw new IllegalStateException("Must specify Switch factory before building Cluster.");
			
			return new Cluster(this);
		}
		
	}
	
	public double getCurrentPowerConsumption() {
		double power = 0;
		
		for (Rack rack : racks)
			power += rack.getCurrentPowerConsumption();
		
		// Add power consumption of the Cluster's Switches.
		power += mainDataSwitch.getPowerConsumption();
		power += mainMgmtSwitch.getPowerConsumption();
		if (nSwitches > 1) {	// Star topology.
			for (Switch s : dataSwitches)
				power += s.getPowerConsumption();
			for (Switch s : mgmtSwitches)
				power += s.getPowerConsumption();
		}		
		
		return power;
	}
	
	public void updateState() {
		// Calculate number of active and suspended Racks -- the rest are powered-off.
		int activeRacks = 0;
		int suspendedRacks = 0;
		for (Rack rack : racks) {
			if (rack.getState() == Rack.RackState.ON)
				activeRacks++;
			else if (rack.getState() == Rack. RackState.SUSPENDED)
				suspendedRacks++;
		}
		
		// Determine Rack's current state.
		if (activeRacks > 0)
			state = ClusterState.ON;
		else if (suspendedRacks > 0)
			state = ClusterState.SUSPENDED;
		else
			state = ClusterState.OFF;
	}
	
	@Override
	public void handleEvent(Event e) {
		// TODO Auto-generated method stub
	}
	
	// Accessor and mutator methods.
	
	public int getId() { return id; }
	
	public int getRackCount() { return nRacks; }
	
	public int getSwitchCount() { return nSwitches; }
	
	public ArrayList<Rack> getRacks() { return racks; }
	
	public ArrayList<Switch> getDataSwitches() { return dataSwitches; }
	
	public ArrayList<Switch> getMgmtSwitches() { return mgmtSwitches; }
	
	public Switch getMainDataSwitch() {	return mainDataSwitch; }
	
	public Switch getMainMgmtSwitch() {	return mainMgmtSwitch; }
	
	public ClusterState getState() { return state; }
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	private int generateHashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, id);
		result = HashCodeUtil.hash(result, nRacks);
		result = HashCodeUtil.hash(result, nSwitches);
		return result;
	}
	
}
