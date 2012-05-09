package edu.uwo.csd.dcsim2.core;

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
	
}
