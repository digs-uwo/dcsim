package edu.uwo.csd.dcsim.core.metrics;

public class ValueMetric extends Metric {

	private double value = 0;
	
	public ValueMetric(String name) {
		super(name);
	}

	public void setValue(double value) {
		this.value = value;
	}
	
	@Override
	public double getValue() {
		return value;
	}

}
