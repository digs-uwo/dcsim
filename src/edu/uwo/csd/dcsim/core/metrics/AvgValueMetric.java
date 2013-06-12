package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class AvgValueMetric extends Metric {

	private double currentValue = 0;
	private long currentCount = 0;
	
	private double value = 0;
	private long count = 0;
	
	public AvgValueMetric(Simulation simulation, String name) {
		super(simulation, name);
	}

	public void addValue(double value) {
		currentValue += value;
		this.value += value;
		
		++currentCount;
		++count;
	}
	
	@Override
	public String format(double value) {
		return Double.toString(Simulation.roundToMetricPrecision(getValue()));
	}

	@Override
	public double getValue() {
		return value / count;
	}

	@Override
	public double getCurrentValue() {
		return currentValue / currentCount;
	}

	@Override
	public void onStartTimeInterval() {
		currentValue = 0;
		currentCount = 0;
	}

	@Override
	public void onCompleteTimeInterval() {
		// nothing to do
	}

	public static AvgValueMetric getMetric(Simulation simulation, String name) {
		AvgValueMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (AvgValueMetric)simulation.getMetric(name);
		}
		else {
			metric = new AvgValueMetric(simulation, name);
			simulation.addMetric(metric);
		}
		return metric;	
	}
	
}
