package edu.uwo.csd.dcsim.host;

import java.util.ArrayList;

/**
 * A network switch within a data centre.
 * 
 * @author Gaston Keller
 *
 */
public class Switch {

	private int power = 0;
	private int nPorts = 0;
	
	private Link upLink = null;
	private ArrayList<Link> ports = null;
	
}
