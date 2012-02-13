package edu.uwo.csd.dcsim2.host;

import java.util.Vector;

public class Processor {
	
	int cores;
	int coreCapacity;

	public Processor(int cores, int coreCapacity) {
		this.cores = cores;
		this.coreCapacity = coreCapacity;
	}
	
	public int getCores() {
		return cores;
	}
	
	public int getCoreCapacity() {
		return coreCapacity;
	}

}
