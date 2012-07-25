package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class AggregateMetric extends Metric {

	private double value = 0;
	private double currentValue = 0;
	
	public AggregateMetric(String name) {
		super(name);
	}
	
	public void addValue(double val) {
		currentValue += val;
		value += val;
	}
	
	public void addCounterAndReset() {
		addValue(getCounter().getValueAndReset());
	}
	
	@Override
	public double getValue() {
		return value;
	}

	@Override
	public double getCurrentValue() {
		return currentValue;
	}

	@Override
	public void resetCurrentValue() {
		currentValue = 0;
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
