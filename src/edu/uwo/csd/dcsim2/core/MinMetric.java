package edu.uwo.csd.dcsim2.core;

public class MinMetric extends Metric {

	private double min = Double.MAX_VALUE;
	
	public MinMetric(String name) {
		super(name);
	}
	
	@Override
	public void addValue(double val) {
		if (val < min)
			min = val;
	}

	@Override
	public double getValue() {
		return min;
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
