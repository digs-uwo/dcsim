package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class FractionalMetric extends Metric {

	private double numerator = 0;
	private double denominator = 0;
	
	private double currentNumerator = 0;
	private double currentDenominator = 0;
	
	public FractionalMetric(String name) {
		super(name);
	}
	
	public void addNumerator(double val) {
		numerator += val;
		currentNumerator += val;
	}
	
	public void addDenominator(double val) {
		denominator += val;
		currentDenominator += val;
	}
	
	public void addValue(double numerator, double denominator) {
		addNumerator(numerator);
		addDenominator(denominator);
	}

	@Override
	public double getValue() {
		return numerator / denominator;
	}

	@Override
	public double getCurrentValue() {
		return currentNumerator / currentDenominator;
	}

	@Override
	public void resetCurrentValue() {
		currentNumerator = 0;
		currentDenominator = 0;
	}

	public static FractionalMetric getSimulationMetric(Simulation simulation, String name) {
		FractionalMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (FractionalMetric)simulation.getMetric(name);
		}
		else {
			metric = new FractionalMetric(name);
			simulation.addMetric(metric);
		}
		return metric;	
	}
	
}
