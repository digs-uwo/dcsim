package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class MinMetric extends Metric {

	private double min = Double.MAX_VALUE;
	
	public MinMetric(String name) {
		super(name);
	}
	
	public void addValue(double val) {
		if (val < min)
			min = val;
	}
	
	public void addCounterAndReset() {
		addValue(getCounter().getValueAndReset());
	}

	@Override
	public double getValue() {
		return min;
	}

	@Override
	public double getCurrentValue() {
		return min; //the 'current' value of min is the same as the value that has been collected since the start of metric recording, since the 'current' set would only have one value
	}

	@Override
	public void resetCurrentValue() {
		//nothing to do
	}
	
	public static MinMetric getSimulationMetric(Simulation simulation, String name) {
		MinMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (MinMetric)simulation.getMetric(name);
		}
		else {
			metric = new MinMetric(name);
			simulation.addMetric(metric);
		}
		return metric;	
	}

}
