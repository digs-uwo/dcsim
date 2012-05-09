package edu.uwo.csd.dcsim2.core;

public class AverageMetric extends Metric {

	private double total = 0;
	private int count = 0;
	
	public AverageMetric(String name) {
		super(name);
	}
	
	public void addValue(double val) {
		total += val;
		++count;
	}

	public void addCounterAndReset() {
		addValue(getCounter().getValueAndReset());
	}
	
	@Override
	public double getValue() {
		return total / count;
	}

	public static AverageMetric getSimulationMetric(Simulation simulation, String name) {
		AverageMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (AverageMetric)simulation.getMetric(name);
		}
		else {
			metric = new AverageMetric(name);
			simulation.addMetric(metric);
		}
		return metric;	
	}
	
}
