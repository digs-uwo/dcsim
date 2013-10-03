package edu.uwo.csd.dcsim.core.metrics;

public class WeightedMetric {

	private double totalWeight = 0;
	private double value = 0;
	private double max = 0;
	private double min = Double.MAX_VALUE;
	private double tempValue = 0;
	
	public void add(double val, double weight) {
		value += val * weight;
		totalWeight += weight;
		
		if (max < val) max = val;
		if (min > val) min = val;
	}
	
	public double getTempValue() {
		return tempValue;
	}
	
	public void setTempValue(double tempValue) {
		this.tempValue = tempValue;
	}
	
	public double getMean() {
		return value / totalWeight;
	}
		
	public double getSum() {
		return value;
	}
	
	public double getMax() {
		return max;
	}
	
	public double getMin() {
		return min;
	}
	
	
	
}
