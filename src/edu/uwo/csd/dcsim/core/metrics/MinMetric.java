package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class MinMetric extends Metric {

	private double min = Double.MAX_VALUE;
	private double count = 0;
	
	public MinMetric(Simulation simulation, String name) {
		super(simulation, name);
	}

	public void incrementCount() {
		++count;
	}

	@Override
	public double getValue() {
		return min;
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
		if (count < min)
			min = count;
	}
	
	public static MinMetric getMetric(Simulation simulation, String name) {
		MinMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (MinMetric)simulation.getMetric(name);
		}
		else {
			metric = new MinMetric(simulation, name);
			simulation.addMetric(metric);
		}
		return metric;	
	}

	@Override
	public String format(double value) {
		return Double.toString(getValue());
	}
	

}
