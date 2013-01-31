package edu.uwo.csd.dcsim.host;

import java.util.ArrayList;

import edu.uwo.csd.dcsim.common.*;
import edu.uwo.csd.dcsim.core.*;

/**
 * A rack within a data centre. Hosts a collection of blades or servers and two
 * switches (data and management networks, respectively).
 * 
 * @author Gaston Keller
 *
 */
public final class Rack implements SimulationEventListener {

	// List of events that the rack can receive.
	// ...
	
	private Simulation simulation;
	
	private int id = 0;
	private int nSlots = 0;				// Number of hosts that can be hosted in the rack.
	private int nHosts = 0;				// Number of hosts actually hosted in the rack.
	
	private ArrayList<Host> hosts = null;						// List of hosts.
	
	//private Switch dataSwitch = new Switch();					// Data network switch.
	//private Switch mgmtSwitch = new Switch();					// Management network switch.
	
	// Do we want a _state_ attribute ???
	
	private Rack(Builder builder) {
		
		this.simulation = builder.simulation;
		
		this.id = simulation.nextId(Rack.class.toString());
		
		this.nSlots = builder.nSlots;
		this.nHosts = builder.nHosts;
		
		this.hosts = new ArrayList<Host>(nHosts);
		for (int i = 0; i < nHosts; i++) {
			this.hosts.add(builder.hostBuilder.build());
		}
		
		// Set default state.
		//state = RackState.OFF;
	}
	
	/**
	 * Builds a new Rack object. This is the only way to instantiate Rack.
	 * 
	 * @author Gaston Keller
	 *
	 */
	public static class Builder implements ObjectBuilder<Rack> {

		private final Simulation simulation;
		
		private int nSlots = 0;
		private int nHosts = 0;
		
		private Host.Builder hostBuilder = null;
		
		public Builder(Simulation simulation) {
			if (simulation == null)
				throw new NullPointerException();
			this.simulation = simulation;
		}
		
		public Builder nSlots(int val) {this.nSlots = val; return this;}
		
		public Builder nHosts(int val) {this.nHosts = val; return this;}
		
		public Builder hostBuilder(Host.Builder hostBuilder) {
			this.hostBuilder = hostBuilder;
			return this;
		}
		
		@Override
		public Rack build() {
			
			if (nSlots == 0 || nHosts == 0)
				throw new IllegalStateException("Must specify Rack's capacity before building Rack.");
			if (nHosts > nSlots)
				throw new IllegalStateException("Number of hosts exceeds Rack's slot capacity.");
			if (hostBuilder == null)
				throw new IllegalStateException("Must specify Host builder before building Rack.");
			
			return new Rack(this);
		}
		
	}
	
	@Override
	public void handleEvent(Event e) {
		// TODO Auto-generated method stub
		
		switch (e.getType()) {
			//case:
			default:
				throw new RuntimeException("Rack #" + getId() + " received unknown event type "+ e.getType());
		}
	}
	
	// Accessor and mutator methods.
	
	public int getId() { return id; }
	
	public int getHostCount() { return nHosts; }
	
	public int getSlotCount() { return nSlots; }
	
	public ArrayList<Host> getHosts() { return hosts; }
	
	//public Switch getDataSwitch() { return dataSwitch; }
	
	//public Switch getMgmtSwitch() { return mgmtSwitch; }

}
