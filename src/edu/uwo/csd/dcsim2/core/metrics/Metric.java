package edu.uwo.csd.dcsim2.core.metrics;

import edu.uwo.csd.dcsim2.core.Utility;


public abstract class Metric {

	private final String name;
	private final Counter counter = new Counter();
	private OutputFormatter outputFormatter = null;
	
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
	
	public OutputFormatter getOutputFormatter() {
		return outputFormatter;
	}
	
	public void setOutputFormatter(OutputFormatter outputFormatter) {
		this.outputFormatter = outputFormatter;
	}
	
	/**
	 * Only sets the OutputFormatter if it has not yet been initialized
	 * @param outputFormatter
	 */
	public void initializeOutputFormatter(OutputFormatter outputFormatter) {
		if (this.outputFormatter == null)
			this.outputFormatter = outputFormatter;
	}

	@Override
	public String toString() {
		if (outputFormatter != null)
			return outputFormatter.format(getValue());
		else
			return Double.toString(getValue());
	}
	
	public String toString(int decimalPlaces) {
		if (outputFormatter != null)
			return outputFormatter.format(getValue(), decimalPlaces);
		else
			return Double.toString(Utility.roundDouble(getValue(), decimalPlaces));
	}
	
	public abstract double getValue();
	
}
