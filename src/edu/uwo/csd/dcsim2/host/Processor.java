package edu.uwo.csd.dcsim2.host;

import java.util.Vector;

public class Processor {
	
	Vector<Core> cores;
	
	public Processor() {
		cores = new Vector<Core>();
	}
	
	public Vector<Core> getCores() {
		return cores;
	}

}
