package edu.uwo.csd.dcsim.host;

/**
 * A network interface card (NIC). To be used by Host class.
 * 
 * @author Gaston Keller
 *
 */
public class NetworkCard implements NetworkingElement {

	private int bandwidth = 0; 			// in KB
	private Link link = null;
	
	/**
	 * Creates an instance of NetworkCard.
	 */
	public NetworkCard (int bandwidth) {
		this.bandwidth = bandwidth;
	}
	
	// Accessor and mutator methods.
	
	public int getBandwidth() { return bandwidth; }
	
	public Link getLink() { return link; }
	
	public void setLink(Link link) { this.link = link; }
	
}
