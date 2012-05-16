package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.common.Utility;
import edu.uwo.csd.dcsim.core.Simulation;


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
		
		if (Simulation.hasProperty("metricPrecision"))
			return toString(Integer.parseInt(Simulation.getProperty("metricPrecision")));
		
		if (outputFormatter != null)
			return outputFormatter.format(getValue());
		else
			return Double.toString(getValue());
	}
	
	public String toString(int precision) {
		if (outputFormatter != null)
			return outputFormatter.format(getValue(), precision);
		else
			return Double.toString(Utility.roundDouble(getValue(), precision));
	}
	
	public abstract double getValue();
	
}
