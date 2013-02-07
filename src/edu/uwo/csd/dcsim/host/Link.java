package edu.uwo.csd.dcsim.host;

/**
 * A network link within a data centre. It connects two instances of 
 * NetworkingElement.
 * 
 * @author Gaston Keller
 *
 */
public class Link {

	private int bw = 0; 			// in KB
	private int bwInUse = 0; 		// in KB
	
	// Links can be Host-Switch or Switch-Switch.
	private final NetworkingElement endpointA;
	private final NetworkingElement endpointB;
	
	/**
	 * Creates an instance of Link.
	 */
	public Link(int bw, NetworkingElement endpointOne, NetworkingElement endpointTwo) {
		this.bw = bw;
		this.endpointA = endpointOne;
		this.endpointB = endpointTwo;
	}
	
	// Accessor and mutator methods.
	
	public int getBw() { return bw;	}
	
	public int getBwInUse() { return bwInUse; }
	
	public void setBwInUse(int bwInUse) { this.bwInUse = bwInUse; }
	
	public NetworkingElement getEndpointOne() { return endpointA; }
	
	public NetworkingElement getEndpointTwo() { return endpointB; }
	
}
