package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class ValueMetric extends Metric {

	private double value = 0;
	
	public ValueMetric(Simulation simulation, String name) {
		super(simulation, name);
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public double getValue() {
		return value;
	}

	@Override
	public double getCurrentValue() {
		return value;
	}

	@Override
	public void onStartTimeInterval() {
		//nothing to do
	}

	@Override
	public void onCompleteTimeInterval() {
		//nothing to do
	}
	
	public static ValueMetric getMetric(Simulation simulation, String name) {
		ValueMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (ValueMetric)simulation.getMetric(name);
		}
		else {
			metric = new ValueMetric(simulation, name);
			simulation.addMetric(metric);
		}
		return metric;	
	}

	@Override
	public String format(double value) {
		return Double.toString(Simulation.roundToMetricPrecision(getValue()));
	}


}
