package edu.uwo.csd.dcsim;

import java.util.Collection;

import edu.uwo.csd.dcsim.core.metrics.Metric;

public interface SimulationTask extends Runnable {
	
	public String getName();
	public long getRandomSeed();
	public void setRandomSeed(long seed);
	public void run();
	public Collection<Metric> getResults();
	
}
