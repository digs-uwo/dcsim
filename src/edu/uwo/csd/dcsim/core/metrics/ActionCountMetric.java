package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class ActionCountMetric extends Metric {

	private double total = 0;
	private double current = 0;
	
	public ActionCountMetric(Simulation simulation, String name) {
		super(simulation, name);
	}

	public void incrementCount() {
		++total;
		++current;
	}
	
	@Override
	public String toString() {
		return Double.toString(getValue());
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
	
	public static ActionCountMetric getMetric(Simulation simulation, String name) {
		ActionCountMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (ActionCountMetric)simulation.getMetric(name);
		}
		else {
			metric = new ActionCountMetric(simulation, name);
			simulation.addMetric(metric);
		}
		return metric;	
	}

}
