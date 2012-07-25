package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class MaxMetric extends Metric {

	private double max = Double.MIN_VALUE;
	
	public MaxMetric(String name) {
		super(name);
	}
	
	public void addValue(double val) {
		if (val > max)
			max = val;
	}
	
	public void addCounterAndReset() {
		addValue(getCounter().getValueAndReset());
	}

	@Override
	public double getValue() {
		return max;
	}

	public static MaxMetric getSimulationMetric(Simulation simulation, String name) {
		MaxMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (MaxMetric)simulation.getMetric(name);
		}
		else {
			metric = new MaxMetric(name);
			simulation.addMetric(metric);
		}
		return metric;	
	}

	@Override
	public double getCurrentValue() {
		//the 'current' value of max is the same as the value that has been collected since the start of metric recording, since the 'current' set would only have one value
		return max;
	}

	@Override
	public void resetCurrentValue() {
		//nothing to do
	}
	
}
