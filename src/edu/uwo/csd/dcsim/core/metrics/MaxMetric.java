package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class MaxMetric extends Metric {

	private double max = Double.MIN_VALUE;
	private double count = 0;
	
	public MaxMetric(Simulation simulation, String name) {
		super(simulation, name);
	}

	public void incrementCount() {
		++count;
	}

	@Override
	public double getValue() {
		return max;
	}

	@Override
	public double getCurrentValue() {
		return count;
	}

	@Override
	public void onStartTimeInterval() {
		count = 0;
	}

	@Override
	public void onCompleteTimeInterval() {
		if (count > max)
			max = count;
	}
	
	public static MaxMetric getMetric(Simulation simulation, String name) {
		MaxMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (MaxMetric)simulation.getMetric(name);
		}
		else {
			metric = new MaxMetric(simulation, name);
			simulation.addMetric(metric);
		}
		return metric;	
	}

	@Override
	public String format(double value) {
		return Double.toString(getValue());
	}
	
}
