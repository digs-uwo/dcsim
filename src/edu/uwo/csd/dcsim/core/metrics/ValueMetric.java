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

	@Override
	public double getCurrentValue() {
		return value; //'current' value is the same as regular value
	}

	@Override
	public void resetCurrentValue() {
		//nothing to do
	}

}
