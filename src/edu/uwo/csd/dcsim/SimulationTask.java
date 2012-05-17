package edu.uwo.csd.dcsim;

import java.util.Collection;

import edu.uwo.csd.dcsim.core.metrics.Metric;

/**
 * SimulationTask defines a type that represents a single configuration and execution of
 * the simulator. Results from the execution of the simulation can be obtained in the form
 * of a Collection of Metric objects via the getResults() method. 
 * 
 * @author Michael Tighe
 *
 */
public interface SimulationTask extends Runnable {
	
	/**
	 * Get the name of the SimulationTask.
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Get the seed used for random values within the simulation execution, so that an
	 * experiment can be repeated.
	 * @return
	 */
	public long getRandomSeed();
	
	/**
	 * Set the seed for random values used in the simulation execution, so that a previous
	 * experiment can be repeated.
	 * @param seed
	 */
	public void setRandomSeed(long seed);
	
	/**
	 * Set the simulation time at which Metric recording should begin. This allows a simulation to
	 * be run for a period of time to allow the system to stabalize prior to collecting metric data.
	 * @param start
	 */
	public void setMetricRecordStart(long start);
	
	/**
	 * Run the simulation task.
	 */
	public void run();
	
	/**
	 * Get the results, which consist of a Collection of Metric objects containing the
	 * metrics gathered by the simulation to describe its outcome.
	 * @return
	 */
	public Collection<Metric> getResults();
	
}
