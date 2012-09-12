package edu.uwo.csd.dcsim;

import java.util.Collection;

import edu.uwo.csd.dcsim.core.metrics.Metric;

/**
 * DCSimulationTaskWrapper wraps a simulation task for execute in order to act as the sole reference to the task, allowing
 * task memory to be freed once it has been run.
 * @author Michael Tighe
 *
 */
public class DCSimulationTaskWrapper implements SimulationTask {

	String name;
	long randomSeed;
	SimulationTask task;
	Collection<Metric> results = null;
	
	public DCSimulationTaskWrapper(SimulationTask task) {
		this.task = task;
		this.name = task.getName();
		this.randomSeed = task.getRandomSeed();
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getRandomSeed() {
		return randomSeed;
	}

	@Override
	public void setRandomSeed(long seed) {
		if (task != null) {
			randomSeed = seed;
			task.setRandomSeed(seed);
		} else {
			throw new IllegalStateException("Attempted to set the random seed of a DCSimulationTaskWrapper that contains no task (or is complete)");
		}
	}

	@Override
	public void setMetricRecordStart(long start) {
		if (task != null) {
			task.setMetricRecordStart(start);
		} else {
			throw new IllegalStateException("Attempted to set the metric record start of a DCSimulationTaskWrapper that contains no task (or is complete)");
		}
	}

	@Override
	public void run() {
		//run the task
		task.run();
		
		//save the results
		results = task.getResults();
		
		//clear the reference to the task in order to free memory
		task = null;
		
		System.gc();
	}

	@Override
	public Collection<Metric> getResults() {
		if (results == null)
			throw new IllegalStateException("Attempted to get results from DCSimulationTaskWrapper which has not executed a task");
		
		return results;
	}

	
	
}
