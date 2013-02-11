package edu.uwo.csd.dcsim.host;

/**
 * Nodes in a network, such as Switches or Network Interface Cards (NICs) in 
 * Hosts, have to implement this interface to be connected by Links.
 * 
 * @author Gaston Keller
 *
 */
public interface NetworkingElement {

	public int getBandwidth();
	
}
