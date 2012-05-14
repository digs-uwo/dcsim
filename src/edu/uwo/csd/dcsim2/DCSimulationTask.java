package edu.uwo.csd.dcsim2;

import java.util.Collection;

import edu.uwo.csd.dcsim2.core.metrics.*;

public abstract class DCSimulationTask implements SimulationTask {

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
	}
	
	@Override
	public final Collection<Metric> getResults() {
		if (!complete)
			throw new IllegalStateException("Simulation task results cannot be obtained until the task has been run");
		
		return metrics;
	}
	
}
