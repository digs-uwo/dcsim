package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class CountMetric extends Metric {

	private double total = 0;
	private double current = 0;
	
	public CountMetric(Simulation simulation, String name) {
		super(simulation, name);
	}

	public void incrementCount() {
		++total;
		++current;
	}
	
	public void add(double value) {
		total += value;
		current += value;
	}
	
	@Override
	public double getValue() {
		return total;
	}

	@Override
	public double getCurrentValue() {
		return current;
	}

	@Override
	public void onStartTimeInterval() {
		current = 0;
	}

	@Override
	public void onCompleteTimeInterval() {
		//nothing to do
	}
	
	public static CountMetric getMetric(Simulation simulation, String name) {
		CountMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (CountMetric)simulation.getMetric(name);
		}
		else {
			metric = new CountMetric(simulation, name);
			simulation.addMetric(metric);
		}
		return metric;	
	}

	@Override
	public String format(double value) {
		return Double.toString(getValue());
	}

}
