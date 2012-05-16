package edu.uwo.csd.dcsim;

import java.util.Collection;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim.core.metrics.*;

public abstract class DCSimulationTask implements SimulationTask {

	private static Logger logger = Logger.getLogger(DCSimulationTask.class);
	
	private final DataCentreSimulation simulation;
	private long duration;
	private long metricRecordStart = 0;
	private Collection<Metric> metrics = null;
	private boolean complete = false;
	
	public DCSimulationTask(String name, long duration) {
		simulation = new DataCentreSimulation(name);
		this.duration = duration;
	}
	
	@Override
	public String getName() {
		return simulation.getName();
	}
	
	@Override
	public final long getRandomSeed() {
		return simulation.getRandomSeed();
	}
	
	@Override
	public final void setRandomSeed(long seed) {
		simulation.setRandomSeed(seed);
	}
	
	public final void setMetricRecordStart(long start) {
		metricRecordStart = start;
	}
	
	public abstract void setup(DataCentreSimulation simulation);
	
	@Override
	public final void run() {
	
		try {
			if (complete)
				throw new IllegalStateException("Simulation task has already been run");
		
			long startTime = System.currentTimeMillis();
			
			setup(simulation);
	
			metrics = simulation.run(duration, metricRecordStart);
			
			long endTime = System.currentTimeMillis();
			
			ValueMetric timeMetric = new ValueMetric("simExecTime");
			timeMetric.setValue(endTime - startTime);
			metrics.add(timeMetric);
			
			complete = true;
		} catch (Exception e) {
			logger.error(simulation.getName() + " failed. " + e);
			e.printStackTrace();
		}
		
	}
	
	@Override
	public final Collection<Metric> getResults() {
		if (!complete)
			throw new IllegalStateException("Simulation task results cannot be obtained until the task has been run");
		
		return metrics;
	}
	
}
