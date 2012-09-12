package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class VmCountMetric extends Metric {

	private double total = 0;
	private double totalWeight = 0;
	
	private double current = 0;
	
	public VmCountMetric(Simulation simulation, String name) {
		super(simulation, name);
	}

	public void incrementVmCount() {
		//increment the current number of VMs
		++current;
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
		//add the current number of vms * the time to the total
		total += current * simulation.getSimulationTime();
		//add the time interval to the total weight
		totalWeight += simulation.getSimulationTime();
	}
	
	public static VmCountMetric getMetric(Simulation simulation, String name) {
		VmCountMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (VmCountMetric)simulation.getMetric(name);
		}
		else {
			metric = new VmCountMetric(simulation, name);
			simulation.addMetric(metric);
		}
		return metric;	
	}

	@Override
	public String format(double value) {
		return Double.toString(Simulation.roundToMetricPrecision(getValue()));
	}
	

}
