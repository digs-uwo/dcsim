package edu.uwo.csd.dcsim.host;

import java.util.ArrayList;

/**
 * A network switch within a data centre.
 * 
 * @author Gaston Keller
 *
 */
public class Switch implements NetworkingElement {

	private int bandwidth = 0; 			// in KB
	private int nPorts = 0;
	private int power = 0;
	
	private Link upLink = null;
	private ArrayList<Link> ports = null;
	
	/**
	 * Creates an instance of Switch.
	 */
	public Switch(int bandwidth, int ports, int power) {
		this.bandwidth = bandwidth;
		this.nPorts = ports;
		this.power = power;
		this.ports = new ArrayList<Link>(ports);
	}
	
	// Accessor and mutator methods.
	
	public int getBandwidth() { return bandwidth; }
	
	public int getPortCount() { return nPorts; }
	
	public int getPowerConsumption() {return power; }
	
	public Link getUpLink() { return upLink; }
	
	public void setUpLink(Link upLink) { this.upLink = upLink; }
	
	public ArrayList<Link> getPorts() {	return ports; }
	
	public void setPorts(ArrayList<Link> ports) throws Exception {
		if (ports.size() < nPorts)
			this.ports = ports;
		else
			throw new RuntimeException("Port capacity exceeded in Switch.");
	}
	
	public void addPort(Link port) {
		if (ports.size() < nPorts)
			ports.add(port);
		else
			throw new RuntimeException("Port capacity exceeded in Switch.");
	}

}
