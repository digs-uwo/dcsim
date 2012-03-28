package edu.uwo.csd.dcsim2.application.workload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

import edu.uwo.csd.dcsim2.core.Simulation;

public class TraceWorkload extends Workload {

	private static Map<String, WorkloadTrace> workloadTraces =  new HashMap<String, WorkloadTrace>();
	
	double scaleFactor;
	WorkloadTrace workloadTrace;
	
	int currentPosition;
	
	public TraceWorkload(String fileName, double scaleFactor, long offset) {
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
		return workloadTrace.getValues().get(currentPosition) * scaleFactor * ((Simulation.getInstance().getSimulationTime() - Simulation.getInstance().getLastUpdate()) / 1000.0);
	}

	@Override
	protected long updateWorkLevel() {
		++currentPosition;
		if (currentPosition >= workloadTrace.getTimes().size())
			currentPosition = 0;
		
		return Simulation.getInstance().getSimulationTime() + workloadTrace.getStepSize();
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
				stepSize = Long.parseLong(line);
				
				int seperator;
				while ((line = input.readLine()) != null) {
					seperator = line.indexOf(',');
					times.add(Long.parseLong(line.substring(0, seperator).trim()));
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
