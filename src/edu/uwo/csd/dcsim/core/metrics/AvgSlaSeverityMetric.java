package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class AvgSlaSeverityMetric extends Metric {

	private double value = 0;
	private double currentValue = 0;
	private long count = 0;
	private long currentCount = 0;
	private double max = 0;
	private double min = 1;
	
	public AvgSlaSeverityMetric(Simulation simulation, String name) {
		super(simulation, name);
	}

	public void addValue(double value) {
		this.value += value;
		this.currentValue += value;
		++count;
		++currentCount;
		
		if (value > max)
			max = value;
		if (value < min)
			min = value;
	}

	@Override
	public double getValue() {
		return value / count;
	}

	@Override
	public double getCurrentValue() {
		return currentValue / currentCount;
	}

	@Override
	public void onStartTimeInterval() {
		currentValue = 0;
		currentCount = 0;
	}

	@Override
	public void onCompleteTimeInterval() {
		//nothing to do
	}

	public static AvgSlaSeverityMetric getMetric(Simulation simulation, String name) {
		AvgSlaSeverityMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (AvgSlaSeverityMetric)simulation.getMetric(name);
		}
		else {
			metric = new AvgSlaSeverityMetric(simulation, name);
			simulation.addMetric(metric);
		}
		return metric;	
	}

	@Override
	public String format(double value) {
		return "[avg|min|max] = [" + Double.toString(Simulation.roundToMetricPrecision(getValue() * 100)) + "%|" +
				Double.toString(Simulation.roundToMetricPrecision(min * 100)) + "%|" + 
				Double.toString(Simulation.roundToMetricPrecision(max * 100)) + "%]";
	}
	
	
}
