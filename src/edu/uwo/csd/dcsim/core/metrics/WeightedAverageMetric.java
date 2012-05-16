package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class WeightedAverageMetric extends Metric {

	private double total = 0;
	private double totalWeight = 0;
	
	public WeightedAverageMetric(String name) {
		super(name);
	}
	
	public void addValue(double val, double weight) {
		total += val * weight;
		totalWeight += weight;
	}

	@Override
	public double getValue() {
		return total / totalWeight;
	}

	public static WeightedAverageMetric getSimulationMetric(Simulation simulation, String name) {
		WeightedAverageMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (WeightedAverageMetric)simulation.getMetric(name);
		}
		else {
			metric = new WeightedAverageMetric(name);
			simulation.addMetric(metric);
		}
		return metric;	
	}
	
}
