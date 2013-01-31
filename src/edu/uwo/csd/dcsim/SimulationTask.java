package edu.uwo.csd.dcsim;

import java.util.Collection;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.core.metrics.Metric;
import edu.uwo.csd.dcsim.core.metrics.ValueMetric;

/**
 * SimulationTask represents a single configuration and execution of a simulation.
 * The setup() method should be overridden and written to setup and configure the simulation.
 * 
 * @author Michael Tighe
 *
 */
public abstract class SimulationTask implements Runnable {
	
	private static Logger logger = Logger.getLogger(SimulationTask.class);
	
	private final Simulation simulation;
	private long duration;
	private long metricRecordStart = 0;
	private Collection<Metric> metrics = null;
	private boolean complete = false;
	
	/**
	 * Construct a SimulationTask.
	 * @param name The name of the simulation task. This can be any name, and is for human reference.
	 * @param duration The duration, in milliseconds, of the simulation.
	 */
	public SimulationTask(String name, long duration) {
		simulation = new Simulation(name);
		this.duration = duration;
	}
	
	public String getName() {
		return simulation.getName();
	}
	
	public long getDuration() {
		return duration;
	}
	
	public long getRandomSeed() {
		return simulation.getRandomSeed();
	}
	
	public void setRandomSeed(long seed) {
		simulation.setRandomSeed(seed);
	}
	
	public void setMetricRecordStart(long start) {
		metricRecordStart = start;
	}
	
	/**
	 * Override to configure the simulation (i.e. create Host objects, VM objects, Services, etc.) 
	 * 
	 * @param simulation
	 */
	public abstract void setup(Simulation simulation);
	
	public void run() {
	
		/**
		 * Note that the simulation is surrounded by a try-catch block that traps all exceptions. This is
		 * done as exceptions can be suppressed when multiple tasks are run in parallel on separate threads. This
		 * ensures that any exception will be reported.
		 */
		try {
			//only allow one execution of this simulation task
			if (complete)
				throw new IllegalStateException("Simulation task has already been run");
		
			long startTime = System.currentTimeMillis();
			
			setup(simulation); //call the setup method
	
			//run the simulation
			metrics = simulation.run(duration, metricRecordStart);
			
			long endTime = System.currentTimeMillis();
			
			//add the real execution time of the simulation to the list of metrics
			ValueMetric timeMetric = new ValueMetric(simulation, "simExecTime");
			timeMetric.setValue(endTime - startTime);
			metrics.add(timeMetric);
			
			complete = true;
		} catch (Exception e) {
			logger.error(simulation.getName() + " failed. " + e);
			e.printStackTrace();
		}
		
	}
	
	public Collection<Metric> getResults() {
		if (!complete)
			throw new IllegalStateException("Simulation task results cannot be obtained until the task has been run");
		
		return metrics;
	}
	
}
