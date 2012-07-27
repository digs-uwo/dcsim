package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class AggregateMetric extends Metric {

	private double value = 0;
	private double currentValue = 0;
	
	public AggregateMetric(Simulation simulation, String name) {
		super(simulation, name);
	}

	public void addValue(double value) {
		this.value += value;
		this.currentValue += value;
	}
	
	@Override
	public String toString() {
		return Double.toString(Simulation.roundToMetricPrecision(getValue()));
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
	public void onStartTimeInterval() {
		currentValue = 0;
	}

	@Override
	public void onCompleteTimeInterval() {
		//nothing to do
	}

	public static AggregateMetric getMetric(Simulation simulation, String name) {
		AggregateMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (AggregateMetric)simulation.getMetric(name);
		}
		else {
			metric = new AggregateMetric(simulation, name);
			simulation.addMetric(metric);
		}
		return metric;	
	}
	
	
}
