package edu.uwo.csd.dcsim.application.workload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

import edu.uwo.csd.dcsim.core.Simulation;

/**
 * TraceWorkload sets the incoming work level based on a trace file. Trace files list workload values 
 * in the range [0, 1]
 * 
 * @author Michael Tighe
 *
 */
public class TraceWorkload extends Workload {

	private static Map<String, WorkloadTrace> workloadTraces =  new HashMap<String, WorkloadTrace>();
	
	double scaleFactor; //the factor by which to scale work values
	WorkloadTrace workloadTrace; //the workload trace
	
	int currentPosition; //the current position in the trace
	
	/**
	 * Create a new TraceWorkload.
	 * @param simulation
	 * @param fileName The file name of the trace to use.
	 * @param scaleFactor The factor by which to scale trace workload values. Traces values are in the range [0, 1], so workload values are in the range [0, scaleFactor]
	 * @param offset The offset in simulation time to start the trace at.
	 */
	public TraceWorkload(Simulation simulation, String fileName, double scaleFactor, long offset) {
		super(simulation);
		
		if (scaleFactor <= 0)
			throw new RuntimeException("Invalid scaleFactor (must be postive): " + scaleFactor);
		
		this.scaleFactor = scaleFactor;
		if (workloadTraces.containsKey(fileName)) {
			workloadTrace = workloadTraces.get(fileName);
		} else {
			workloadTrace = new WorkloadTrace(fileName);
			workloadTraces.put(fileName, workloadTrace);
		}
		
		currentPosition = (int)Math.floor((offset % (workloadTrace.getLastTime() + workloadTrace.stepSize)) / workloadTrace.stepSize) - 1;
	}
	
	@Override
	protected double retrievePendingWork() {
		return workloadTrace.getValues().get(currentPosition) * scaleFactor * ((simulation.getElapsedTime()) / 1000.0);
	}

	@Override
	protected long updateWorkLevel() {
		++currentPosition;
		if (currentPosition >= workloadTrace.getTimes().size())
			currentPosition = 0;
		
		/*
		 * Calculate the update time so that the event times are always divisible by the step size. This ensures that regardless
		 * of when the workload is created and started, all workloads with the same step size will update at the same time. This 
		 * is a performance optimization to reduce the number of time jumps in the simulation.
		 */
		return (simulation.getSimulationTime() - (simulation.getSimulationTime() % workloadTrace.getStepSize())) + workloadTrace.getStepSize();
	}
	
	private class WorkloadTrace {
		private ArrayList<Long> times;
		private ArrayList<Double> values;
		private Long stepSize;
		
		public WorkloadTrace(String fileName) {
			times = new ArrayList<Long>();
			values = new ArrayList<Double>();
			
			try {
				BufferedReader input = new BufferedReader(new FileReader(fileName));
	
				String line;
				
				//read first line, which should contain the step size
				line = input.readLine();
				stepSize = Long.parseLong(line) * 1000; //file is in seconds, simulation runs in ms
				
				int seperator;
				while ((line = input.readLine()) != null) {
					seperator = line.indexOf(',');
					times.add(Long.parseLong(line.substring(0, seperator).trim()) * 1000); //file is in seconds, simulation runs in ms
					values.add(Double.parseDouble(line.substring(seperator + 1).trim()));
				}
				
				input.close();
				
				//if 0 not first, assume (0, 0) as initial time/workload pair
				if (times.get(0) != 0) {
					times.add(0, 0l);
					values.add(0, 0.0);
				}
		
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Could not find trace file '" + fileName + "'", e);
			} catch (IOException e) {
				throw new RuntimeException("Could not load trace file '" + fileName + "'", e);
			}	
			
		}
				
		public ArrayList<Long> getTimes() {
			return times;
		}
		
		public long getLastTime() {
			return times.get(times.size() -1 );
		}
		
		public ArrayList<Double> getValues() {
			return values;
		}
		
		public long getStepSize() {
			return stepSize;
		}
	}

}
