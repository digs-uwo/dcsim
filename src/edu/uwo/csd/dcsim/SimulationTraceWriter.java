package edu.uwo.csd.dcsim;

import java.io.*;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.core.metrics.Metric;
import edu.uwo.csd.dcsim.core.metrics.Metric.MetricRecord;

public class SimulationTraceWriter {

	private static Logger logger = Logger.getLogger(SimulationTraceWriter.class);
	
	private SimulationTask task;
	
	public SimulationTraceWriter(SimulationTask task) {
		this.task = task;
	}
	
	public void writeTrace() {
		
		long startTime = System.currentTimeMillis();
		
		try {
			FileWriter fstream = new FileWriter(Simulation.getOutputDirectory() + "/" + task.getName() + ".simtrace");
			BufferedWriter out = new BufferedWriter(fstream);
			
			for (Metric metric : task.getResults()) {
				for (MetricRecord record : metric.getRecordedValues()) {
					out.write(record.time + "," + metric.getName() + "," + record.value);
					out.newLine();
				}
			}
			out.close();
		} catch (IOException e) {
			logger.error("Could not write to simulation trace file", e);
		}
		
		long elapsed = System.currentTimeMillis() - startTime;
		
		logger.info("Wrote trace to file in " + elapsed + "ms");
		
	}
	
}
