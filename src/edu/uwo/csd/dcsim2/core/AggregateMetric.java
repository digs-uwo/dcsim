package edu.uwo.csd.dcsim2.core;

public class AggregateMetric extends Metric {

	private double value = 0;
	
	public AggregateMetric(String name) {
		super(name);
	}
	
	@Override
	public void addValue(double val) {
		value += val;
	}
	
	@Override
	public double getValue() {
		return value;
	}

	public static AggregateMetric getSimulationMetric(Simulation simulation, String name) {
		AggregateMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (AggregateMetric)simulation.getMetric(name);
		}
		else {
			metric = new AggregateMetric(name);
			simulation.addMetric(metric);
		}
		return metric;	
	}

	
}
