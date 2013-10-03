package edu.uwo.csd.dcsim.core.metrics;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class Metric {

	private double tempValue = 0;
	
	SummaryStatistics stats = new SummaryStatistics();
	
	public void add(double val) {
		stats.addValue(val);
	}
	
	public double getTempValue() {
		return tempValue;
	}
	
	public void setTempValue(double tempValue) {
		this.tempValue = tempValue;
	}
	
	public double getMean() {
		return stats.getMean();
	}
	
	public double getVariance() {
		return stats.getVariance();
	}
	
	public double getSum() {
		return stats.getSum();
	}
	
	public double getMax() {
		return stats.getMax();
	}
	
	public double getMin() {
		return stats.getMin();
	}
	
	
	
}
