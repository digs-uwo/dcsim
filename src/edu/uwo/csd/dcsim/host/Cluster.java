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

	// List of events that the cluster can receive.
	// ...
	
	private Simulation simulation;
	
	private int id = 0;
	private int nRacks = 0;				// Number of racks in the cluster.
	
	private ArrayList<Rack> racks = null;						// List of racks.
	
	//private ArrayList<Switch> dataNet = new ArrayList<Switch>();					// Data network switches.
	//private ArrayList<Switch> mgmtNet = new ArrayList<Switch>();					// Management network switches.
	
	// Do we want a _state_ attribute ???
	
	private Cluster(Builder builder) {
		
		this.simulation = builder.simulation;
		
		this.id = simulation.nextId(Cluster.class.toString());
		
		this.nRacks = builder.nRacks;
		
		this.racks = new ArrayList<Rack>(nRacks);
		for (int i = 0; i < nRacks; i++) {
			this.racks.add(builder.rackBuilder.build());
		}
		
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
		
		private Rack.Builder rackBuilder = null;
		
		public Builder(Simulation simulation) {
			if (simulation == null)
				throw new NullPointerException();
			this.simulation = simulation;
		}
		
		public Builder nRacks(int val) {this.nRacks = val; return this;}
		
		public Builder rackBuilder(Rack.Builder rackBuilder) {
			this.rackBuilder = rackBuilder;
			return this;
		}
		
		@Override
		public Cluster build() {
			
			if (nRacks == 0)
				throw new IllegalStateException("Must specify number of racks before building Cluster.");
			if (rackBuilder == null)
				throw new IllegalStateException("Must specify Rack builder before building Cluster.");
			
			return new Cluster(this);
		}
		
	}
	
	@Override
	public void handleEvent(Event e) {
		// TODO Auto-generated method stub
		
		switch (e.getType()) {
			//case:
			default:
				throw new RuntimeException("Cluster #" + getId() + " received unknown event type "+ e.getType());
		}
	}
	
	// Accessor and mutator methods.
	
	public int getId() { return id; }
	
	public int getRackCount() { return nRacks; }
	
	public ArrayList<Rack> getRacks() { return racks; }
	
	//public ArrayList<Switch> getDataNet() { return dataNet; }
	
	//public ArrayList<Switch> getMgmtNet() { return mgmtNet; }
	
}
