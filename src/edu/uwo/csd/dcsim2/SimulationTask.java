package edu.uwo.csd.dcsim2;

import java.util.Collection;

import edu.uwo.csd.dcsim2.core.metrics.Metric;

public interface SimulationTask extends Runnable {
	
	public String getName();
	public long getRandomSeed();
	public void setRandomSeed(long seed);
	public void run();
	public Collection<Metric> getResults();
	
}
