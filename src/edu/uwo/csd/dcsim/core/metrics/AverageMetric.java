package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class AverageMetric extends Metric {

	private double total = 0;
	private int count = 0;
	
	private double currentTotal = 0;
	private double currentCount = 0;
	
	public AverageMetric(String name) {
		super(name);
	}
	
	public void addValue(double val) {
		total += val;
		++count;
		
		currentTotal += val;
		++currentCount;
	}

	public void addCounterAndReset() {
		addValue(getCounter().getValueAndReset());
	}
	
	@Override
	public double getValue() {
		return total / count;
	}

	@Override
	public double getCurrentValue() {
		return currentTotal / currentCount;
	}

	@Override
	public void resetCurrentValue() {
		currentTotal = 0;
		currentCount = 0;
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
