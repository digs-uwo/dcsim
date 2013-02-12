package edu.uwo.csd.dcsim.host;

import java.util.ArrayList;

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
	private int nRacks = 0;				// Number of racks in the cluster.
	private int nSwitches = 0;			// Number of switches in the cluster.
	
	private ArrayList<Rack> racks = null;						// List of racks.
	
	private ArrayList<Switch> dataSwitches = null;				// Data network switches.
	private ArrayList<Switch> mgmtSwitches = null;				// Management network switches.
	
	// Do we want a _state_ attribute ???
	
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
		
		// TODO Connect switches (in both networks)
		
		// Are we connecting the switches (in both networks) to each other 
		// (mesh topology), or are we adding a central, higher-level switch to 
		// which all other switches are connected (star topology)?		
		
		// Set default state.
		//state = RackState.OFF;
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
	
}
