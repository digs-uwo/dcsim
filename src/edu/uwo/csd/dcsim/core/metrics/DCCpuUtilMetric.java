package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class DCCpuUtilMetric extends Metric {

	private double totalInUse = 0;
	private double totalCapacity = 0;
	
	private double currentInUse = 0;
	private double currentCapacity = 0;
	
	public DCCpuUtilMetric(Simulation simulation, String name) {
		super(simulation, name);
	}

	public void addHostUse(double inUse, double capacity) {
		totalInUse += inUse * simulation.getElapsedSeconds();
		totalCapacity += capacity * simulation.getElapsedSeconds();
		
		currentInUse += inUse;
		currentCapacity += capacity;
	}

	@Override
	public double getValue() {
		return totalInUse / totalCapacity;
	}

	@Override
	public double getCurrentValue() {
		return currentInUse / currentCapacity;
	}

	@Override
	public void onStartTimeInterval() {
		currentInUse = 0;
		currentCapacity = 0;
	}

	@Override
	public void onCompleteTimeInterval() {
		//nothing to do
	}

	public static DCCpuUtilMetric getMetric(Simulation simulation, String name) {
		DCCpuUtilMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (DCCpuUtilMetric)simulation.getMetric(name);
		}
		else {
			metric = new DCCpuUtilMetric(simulation, name);
			simulation.addMetric(metric);
		}
		return metric;	
	}

	@Override
	public String format(double value) {
		return Double.toString(Simulation.roundToMetricPrecision(getValue() * 100)) + "%";
	}
	
}
