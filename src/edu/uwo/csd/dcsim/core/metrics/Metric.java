package edu.uwo.csd.dcsim.core.metrics;

import java.util.*;

import edu.uwo.csd.dcsim.core.Simulation;

public abstract class Metric {

	protected Simulation simulation;
	private final String name;
	private ArrayList<MetricRecord> recordedValues =  new ArrayList<MetricRecord>();
	
	public Metric(Simulation simulation, String name) {
		this.simulation = simulation;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<MetricRecord> getRecordedValues() {
		return recordedValues;
	}

	/**
	 * Format an arbitrary value of the type recorded by this metric.
	 * @param value
	 * @return
	 */
	public abstract String format(double value);
	
	@Override
	public String toString() {
		return format(getValue());
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
	
	public abstract void onStartTimeInterval();
	public abstract void onCompleteTimeInterval();
	
	public final void startTimeInterval() {
		onStartTimeInterval();
	}
	
	public final void completeTimeInterval() {
		onCompleteTimeInterval();
		MetricRecord record = new MetricRecord(simulation.getSimulationTime(), getCurrentValue());
		recordedValues.add(record);
	}
	
	public class MetricRecord {
		
		public final long time;
		public final double value;
		
		public MetricRecord(long time, double value) {
			this.time = time;
			this.value = value;
		}
		
	}
	
}
