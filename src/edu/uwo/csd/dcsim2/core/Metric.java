package edu.uwo.csd.dcsim2.core;

public abstract class Metric {

	private final String name;
	private final Counter counter = new Counter();
	
	public Metric(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Counter getCounter() {
		return counter;
	}
	
	public void addCounterValue() {
		addValue(counter.getValue());
	}
	
	public void addCounterAndReset() {
		addCounterValue();
		counter.reset();
	}
	
	public void incrementCounter() {
		counter.increment();
	}
	
	public void resetCounter() {
		counter.reset();
	}
	
	public abstract void addValue(double val);
	public abstract double getValue();
	
}
