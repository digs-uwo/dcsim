package edu.uwo.csd.dcsim.core.metrics;

import java.util.*;

import edu.uwo.csd.dcsim.common.Utility;
import edu.uwo.csd.dcsim.core.Simulation;


public abstract class Metric {

	private final String name;
	private final Counter counter = new Counter();
	private OutputFormatter outputFormatter = null;
	private ArrayList<MetricRecord> recordedValues =  new ArrayList<MetricRecord>();
	
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
	
	public ArrayList<MetricRecord> getRecordedValues() {
		return recordedValues;
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
	
	/**
	 * This gets the value of the metric as calculated since the start of metric recording
	 * @return
	 */
	public abstract double getValue();
	
	/**
	 * This gets the value of the metric over the current time interval
	 * @return
	 */
	public abstract double getCurrentValue();
	
	/**
	 * Reset the current value
	 */
	public abstract void resetCurrentValue();
	
	public void completeTimeInterval(Simulation simulation) {
		MetricRecord record = new MetricRecord(simulation.getSimulationTime(), getCurrentValue());
		recordedValues.add(record);
		resetCurrentValue();
	}
	
	public class MetricRecord {
		
		public final long time;
		public final double value;
		
		public MetricRecord(long time, double value) {
			this.time = time;
			this.value = value;
		}
		
		@Override 
		public String toString() {
			if (Simulation.hasProperty("metricPrecision"))
				return toString(Integer.parseInt(Simulation.getProperty("metricPrecision")));
			
			if (Metric.this.outputFormatter != null) {
				return Metric.this.outputFormatter.formatNoUnits(value);
			} else {
				return Double.toString(value);
			}
		}
		
		public String toString(int precision) {
			if (outputFormatter != null)
				return outputFormatter.formatNoUnits(value, precision);
			else
				return Double.toString(Utility.roundDouble(value, precision));
		}
		
	}
	
}
