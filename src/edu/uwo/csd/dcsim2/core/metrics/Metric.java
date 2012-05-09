package edu.uwo.csd.dcsim2.core.metrics;


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
	
	public void incrementCounter() {
		counter.increment();
	}
	
	public void resetCounter() {
		counter.reset();
	}

	public abstract double getValue();
	
}
