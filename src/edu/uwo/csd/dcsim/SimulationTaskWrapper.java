package edu.uwo.csd.dcsim;

import java.util.Collection;

import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.core.metrics.Metric;

/**
 * SimulationTaskWrapper wraps a simulation task for execute in order to act as the sole reference to the task, allowing
 * task memory to be freed once it has been run.
 * @author Michael Tighe
 *
 */
public class SimulationTaskWrapper extends SimulationTask {

	SimulationTask task;
	Collection<Metric> results = null;
	
	public SimulationTaskWrapper(SimulationTask task) {
		super(task.getName(), task.getDuration());
		this.task = task;
	}
	
	@Override
	public String getName() {
		return task.getName();
	}

	@Override
	public long getRandomSeed() {
		return task.getRandomSeed();
	}

	@Override
	public void setRandomSeed(long seed) {
		if (task != null) {
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

	@Override
	public void setup(Simulation simulation) {
		// nothing to do
	}

	
	
}
