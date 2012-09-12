package edu.uwo.csd.dcsim;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.stat.descriptive.*;
import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim.core.metrics.Metric;

public class DCSimulationReport {

	private static Logger logger = Logger.getLogger(DCSimulationReport.class);
	
	private String name;
	private List<? extends SimulationTask> tasks;
	private Map<String, DescriptiveStatistics> metricStats = new HashMap<String, DescriptiveStatistics>();
	
	public DCSimulationReport(ArrayList<? extends SimulationTask> tasks) {
		this(tasks.get(0).getName(), tasks);
	}
	
	public DCSimulationReport(String name, ArrayList<? extends SimulationTask> tasks) {
		this.tasks = tasks;
		this.name = name;
		
		/*
		 * Build descriptive statistics for each metric.
		 * Metrics present in some tasks but not in others are considered to have a value of 0.
		 */
		
		//Initialize the metric stats map with all metric names found in any task. This requires checking every task for metrics.
		for (SimulationTask task : tasks) {
			for (Metric metric : task.getResults()) {
				if (!metricStats.containsKey(metric.getName())) {
					metricStats.put(metric.getName(), new DescriptiveStatistics());
				}
			}
		}
		
		//Iterate through all tasks and add their metric values
		for (SimulationTask task : tasks) {
			for (String metricName : metricStats.keySet()) {
				//set the value to 0 by default, in case the metric is not present in this task
				double val = 0;
				
				//find the metric value
				for (Metric metric : task.getResults()) {
					if (metric.getName().equals(metricName)) {
						val = metric.getValue();
					}
				}
				
				//add the value to the metric statistics
				metricStats.get(metricName).addValue(val);
			}
		}
	}

	public void logResults() {
		
		logger.info("Results for " + name + " (" + tasks.size() + " repetitions)");
		
		//sort metrics by name
		ArrayList<String> metricNames = new ArrayList<String>(metricStats.keySet());
		Collections.sort(metricNames);
		
		//output metrics in alphabetical order
		for (String metricName : metricNames) {
			DescriptiveStatistics stats = metricStats.get(metricName);
			logger.info(metricName + ": " + 
					stats.getMean() + " [" + 
					stats.getStandardDeviation() + "], Range [" + 
					stats.getMin() + "," + 
					stats.getMax() + "]");
		}
		
	}
	
	public void writeCsv(BufferedWriter out) throws IOException {

		//sort metrics by name
		ArrayList<String> metricNames = new ArrayList<String>(metricStats.keySet());
		Collections.sort(metricNames);

		//write headers
		out.write("task#, ");
		for (int i = 0; i < metricNames.size(); ++i) {
			out.write(metricNames.get(i));
			if (i != metricNames.size() - 1)
				out.write(", ");
		}
		out.newLine();
		
		//write simulation task values
		for (int i = 0; i < tasks.size(); ++i) {
			out.write(i + ", ");
			
			for (int j = 0; j < metricNames.size(); ++j) {
				out.write(Double.toString(metricStats.get(metricNames.get(j)).getValues()[i]));
				if (j != metricNames.size() - 1)
					out.write(", ");
			}
			
			out.newLine();
		}
		
//		//write average values
//		out.write("avg, ");
//		for (int i = 0; i < metricNames.size(); ++i) {
//			out.write(Double.toString(metricStats.get(metricNames.get(i)).getMean()));
//			if (i != metricNames.size() -1)
//				out.write(", ");
//		}
//		out.newLine();
//		
//		//write max values
//		out.write("max, ");
//		for (int i = 0; i < metricNames.size(); ++i) {
//			out.write(Double.toString(metricStats.get(metricNames.get(i)).getMax()));
//			if (i != metricNames.size() -1)
//				out.write(", ");
//		}
//		out.newLine();
//		
//		//write min values
//		out.write("min, ");
//		for (int i = 0; i < metricNames.size(); ++i) {
//			out.write(Double.toString(metricStats.get(metricNames.get(i)).getMin()));
//			if (i != metricNames.size() -1)
//				out.write(", ");
//		}
//		out.newLine();
//		
//		//write std.dev. values
//		out.write("stdev, ");
//		for (int i = 0; i < metricNames.size(); ++i) {
//			out.write(Double.toString(metricStats.get(metricNames.get(i)).getStandardDeviation()));
//			if (i != metricNames.size() -1)
//				out.write(", ");
//		}
//		out.newLine();

	}
	
	public String getName() {
		return name;
	}
	
}
