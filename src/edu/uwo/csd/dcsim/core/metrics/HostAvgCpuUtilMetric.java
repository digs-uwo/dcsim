package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class HostAvgCpuUtilMetric extends Metric {

	/*
	 * Utilization for the entire simulation
	 */
	private double total = 0;			//the total of host utilization values
	private double totalWeight = 0; 	//weight is essentially equivalent to active host time
	
	/*
	 * Utilization values for current time interval. Note that this is not weighted by time, so that values
	 * are directly comparable. Time information is retained in the Metric Record
	 */
	private double currentTotal = 0;	//current total of host utilization values
	private double currentCount = 0;	//current number of hosts
	
	public HostAvgCpuUtilMetric(Simulation simulation, String name) {
		super(simulation, name);
	}

	public void addHostUtilization(double hostUtil) {
		total += hostUtil * simulation.getElapsedTime();
		totalWeight += simulation.getElapsedTime();
		
		currentTotal += hostUtil;
		++currentCount;
	}

	@Override
	public double getValue() {
		return total / totalWeight;
	}

	@Override
	public double getCurrentValue() {
		return currentTotal / currentCount;
	}

	@Override
	public void onStartTimeInterval() {
		currentTotal = 0;
		currentCount = 0;
	}

	@Override
	public void onCompleteTimeInterval() {
		//nothing to do
	}
	
	public static HostAvgCpuUtilMetric getMetric(Simulation simulation, String name) {
		HostAvgCpuUtilMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (HostAvgCpuUtilMetric)simulation.getMetric(name);
		}
		else {
			metric = new HostAvgCpuUtilMetric(simulation, name);
			simulation.addMetric(metric);
		}
		return metric;	
	}

	@Override
	public String format(double value) {
		return Double.toString(Simulation.roundToMetricPrecision(getValue() * 100)) + "%";
	}

}
