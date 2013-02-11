package edu.uwo.csd.dcsim.host;

/**
 * A network link within a data centre. It connects two instances of 
 * NetworkingElement. Its bandwidth is defined at creation time, taking the 
 * minimum bandwidth of the two Networking Elements it connects.
 * 
 * @author Gaston Keller
 *
 */
public class Link {

	private int bandwidth = 0; 				// in KB
	private int bandwidthInUse = 0; 		// in KB
	
	// Links can be Host-Switch or Switch-Switch.
	private final NetworkingElement endpointA;
	private final NetworkingElement endpointB;
	
	/**
	 * Creates an instance of Link.
	 */
	public Link(NetworkingElement endpointOne, NetworkingElement endpointTwo) {
		this.endpointA = endpointOne;
		this.endpointB = endpointTwo;
		this.bandwidth = Math.min(endpointOne.getBandwidth(), endpointTwo.getBandwidth());
	}
	
	// Accessor and mutator methods.
	
	public int getBandwidth() { return bandwidth; }
	
	public int getBandwidthInUse() { return bandwidthInUse; }
	
	public void setBandwidthInUse(int bandwidthInUse) { this.bandwidthInUse = bandwidthInUse; }
	
	public NetworkingElement getEndpointOne() { return endpointA; }
	
	public NetworkingElement getEndpointTwo() { return endpointB; }
	
}
