package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class ActiveHostMetric extends Metric {

	private double total = 0;
	private double totalWeight = 0;
	
	private double current = 0;
	
	public ActiveHostMetric(Simulation simulation, String name) {
		super(simulation, name);
	}

	public void incrementHostCount() {
		//increment the current number of active hosts
		++current;
	}
	
	@Override
	public String toString() {
		return Double.toString(Simulation.roundToMetricPrecision(getValue()));
	}

	@Override
	public double getValue() {
		return total / totalWeight;
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
		//add the current number of hosts * the time they were active to the total
		total += current * simulation.getSimulationTime();
		//add the time interval to the total weight
		totalWeight += simulation.getSimulationTime();
	}
	
	public static ActiveHostMetric getMetric(Simulation simulation, String name) {
		ActiveHostMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (ActiveHostMetric)simulation.getMetric(name);
		}
		else {
			metric = new ActiveHostMetric(simulation, name);
			simulation.addMetric(metric);
		}
		return metric;	
	}
	

}
