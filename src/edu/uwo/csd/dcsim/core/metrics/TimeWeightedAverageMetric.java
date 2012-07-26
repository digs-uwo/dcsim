package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class TimeWeightedAverageMetric extends Metric {

	private double total = 0;
	private double totalWeight = 0;
	
	private double currentTotal = 0;
	/*
	 * Note that we calculate a non-weighted average for the current value, since all weights are the same in one interval. If we used weights, then the average values
	 * for each interval would not be comparable. The actual time that the current value is valid for is recorded in the MetricRecord, so time value is retained.
	 */
	private double currentTotalCount = 0;
	
	public TimeWeightedAverageMetric(String name) {
		super(name);
	}
	
	public void addValue(double val, double elapsedTime) {
		currentTotal += val * elapsedTime;
		currentTotalCount++;
		
		total += val * elapsedTime;
		totalWeight += elapsedTime;
	}

	@Override
	public double getValue() {
		return total / totalWeight;
	}

	@Override
	public double getCurrentValue() {
		return currentTotal / currentTotalCount;
	}

	@Override
	public void resetCurrentValue() {
		currentTotal = 0;
		currentTotalCount = 0;
	}
	
	public static TimeWeightedAverageMetric getSimulationMetric(Simulation simulation, String name) {
		TimeWeightedAverageMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (TimeWeightedAverageMetric)simulation.getMetric(name);
		}
		else {
			metric = new TimeWeightedAverageMetric(name);
			simulation.addMetric(metric);
		}
		return metric;	
	}
	
}
