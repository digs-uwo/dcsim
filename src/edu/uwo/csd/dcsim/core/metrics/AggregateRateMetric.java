package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class AggregateRateMetric extends Metric {

	private double value = 0;
	private double currentValue = 0;
	
	public AggregateRateMetric(String name) {
		super(name);
	}
	
	public void addValue(double val, double elapsedSeconds) {
		currentValue += val; //current value does not include elapsed time, but is rather the instantaneous time. Time is retained though the 'time' value in the MetricRecord.
		value += val * elapsedSeconds;
	}
	
	@Override
	public double getValue() {
		return value;
	}

	@Override
	public double getCurrentValue() {
		return currentValue;
	}

	@Override
	public void resetCurrentValue() {
		currentValue = 0;
	}
	
	public static AggregateRateMetric getSimulationMetric(Simulation simulation, String name) {
		AggregateRateMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (AggregateRateMetric)simulation.getMetric(name);
		}
		else {
			metric = new AggregateRateMetric(name);
			simulation.addMetric(metric);
		}
		return metric;	
	}

	
}
