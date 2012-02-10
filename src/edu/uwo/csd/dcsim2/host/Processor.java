package edu.uwo.csd.dcsim2.host;

import java.util.Vector;

public class Processor {
	
	Vector<Core> cores;
	
	public Processor() {
		cores = new Vector<Core>();
	}
	
	public Processor(int nCores, int coreCapacity) {
		cores = new Vector<Core>();
		for (int i = 0; i < nCores; ++i) {
			cores.add(new Core(coreCapacity));
		}
	}
	
	public Vector<Core> getCores() {
		return cores;
	}

}
