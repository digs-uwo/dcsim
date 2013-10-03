package edu.uwo.csd.dcsim.core.metrics;

import java.io.PrintStream;
import java.util.*;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim.common.SimTime;
import edu.uwo.csd.dcsim.common.Tuple;
import edu.uwo.csd.dcsim.core.Simulation;

public class SimulationMetrics {

	Simulation simulation;
	HostMetrics hostMetrics;
	ApplicationMetrics applicationMetrics;
	ManagementMetrics managementMetrics;
	Map<Class<? extends MetricCollection>, MetricCollection> customMetrics = new HashMap<Class<? extends MetricCollection>, MetricCollection>();
	
	long executionTime;
	int applicationSchedulingTimedOut = 0;
	
	public SimulationMetrics(Simulation simulation) {
		this.simulation = simulation;
		
		hostMetrics = new HostMetrics(simulation);
		applicationMetrics = new ApplicationMetrics(simulation);
		managementMetrics = new ManagementMetrics(simulation);
	}
	
	public HostMetrics getHostMetrics() {
		return hostMetrics;
	}
	
	public ApplicationMetrics getApplicationMetrics() {
		return applicationMetrics;
	}
	
	public ManagementMetrics getManagementMetrics() {
		return managementMetrics;
	}
	
	public void completeSimulation() {
		hostMetrics.completeSimulation();
		applicationMetrics.completeSimulation();
		managementMetrics.completeSimulation();
	}
	
	public void setExecutionTime(long time) {
		executionTime = time;
	}
	
	public long getExecutionTime() {
		return executionTime;
	}
	
	public int getApplicationSchedulingTimedOut() {
		return applicationSchedulingTimedOut;
	}
	
	public void setApplicationSchedulingTimedOut(int applicationSchedulingTimedOut) {
		this.applicationSchedulingTimedOut = applicationSchedulingTimedOut;
	}
	
	public void incrementApplicationSchedulingTimedOut() {
		++applicationSchedulingTimedOut;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends MetricCollection> T getCustomMetricCollection(Class<T> type) {
		return (T)customMetrics.get(type);
	}
	
	public void addCustomMetricCollection(MetricCollection metricCollection) {
		customMetrics.put(metricCollection.getClass(), metricCollection);
	}
	
	public void printDefault(Logger out) {
		
		hostMetrics.printDefault(out);
		out.info("");
		applicationMetrics.printDefault(out);
		out.info("");
		managementMetrics.printDefault(out);
		out.info("");
		
		for (MetricCollection metrics : customMetrics.values()) {
			metrics.printDefault(out);
			out.info("");
		}

		out.info("-- SIMULATION --");
		out.info("   execution time: " + SimTime.toHumanReadable(getExecutionTime()));
		out.info("   simulated time: " + SimTime.toHumanReadable(simulation.getDuration()));
		out.info("   metric recording start: " + SimTime.toHumanReadable(simulation.getMetricRecordStart()));
		out.info("   metric recording duration: " + SimTime.toHumanReadable(simulation.getDuration() - simulation.getMetricRecordStart()));
		out.info("   application scheduling timed out: " + applicationSchedulingTimedOut);
		
	}
	
	public List<Tuple<String, Object>> getMetricValues() {
		List<Tuple<String, Object>> metrics = new ArrayList<Tuple<String, Object>>();
		
		metrics.add(new Tuple<String, Object>("executionTime", getExecutionTime()));
		metrics.add(new Tuple<String, Object>("simulatedTime", simulation.getDuration()));
		metrics.add(new Tuple<String, Object>("metricRecordStart", simulation.getMetricRecordStart()));
		metrics.add(new Tuple<String, Object>("metricRecordDuration", simulation.getDuration() - simulation.getMetricRecordStart()));
		metrics.add(new Tuple<String, Object>("appSchedulingTimeout", applicationSchedulingTimedOut));

		metrics.addAll(hostMetrics.getMetricValues());
		metrics.addAll(applicationMetrics.getMetricValues());
		metrics.addAll(managementMetrics.getMetricValues());
		
		for (MetricCollection custom : customMetrics.values()) {
			metrics.addAll(custom.getMetricValues());
		}
		
		return metrics;
	}
	
	public void printCSV(PrintStream out) {
		printCSV(out, true);
	}
	
	public void printCSV(PrintStream out, boolean headings) {
		List<Tuple<String, Object>> metrics = getMetricValues();

		if (headings) {
			out.print("name");
			for (Tuple<String, Object> metric : metrics) {
				out.print("," + metric.a);
			}
			out.println("");
		}

		out.print(simulation.getName());
		for (Tuple<String, Object> metric : metrics) {
			out.print("," + metric.b);
		}
		out.println("");
	}
}
