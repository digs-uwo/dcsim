package edu.uwo.csd.dcsim2.application.workload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

import edu.uwo.csd.dcsim2.core.Simulation;

public class PlanetLabTraceWorkload extends Workload {

	private static Map<String, PlanetLabWorkloadTrace> workloadTraces =  new HashMap<String, PlanetLabWorkloadTrace>();
	
	double scaleFactor;
	PlanetLabWorkloadTrace workloadTrace;
	
	int currentPosition;
	
	public PlanetLabTraceWorkload(String fileName, double scaleFactor, long offset) {
		this(fileName, scaleFactor, offset, 5000);
	}
	
	public PlanetLabTraceWorkload(String fileName, double scaleFactor, long offset, long stepSize) {
		this.scaleFactor = scaleFactor;
		if (workloadTraces.containsKey(fileName)) {
			workloadTrace = workloadTraces.get(fileName);
		} else {
			workloadTrace = new PlanetLabWorkloadTrace(fileName, stepSize);
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
		if (currentPosition >= workloadTrace.getValues().size())
			currentPosition = 0;
		
		return Simulation.getInstance().getSimulationTime() + workloadTrace.getStepSize();
	}
	
	private class PlanetLabWorkloadTrace {
		private ArrayList<Double> values;
		private long stepSize;
		
		public PlanetLabWorkloadTrace(String fileName, long stepSize) {
			this.stepSize = stepSize;
			
			values = new ArrayList<Double>();
			
			try {
				BufferedReader input = new BufferedReader(new FileReader(fileName));
	
				String line;
							
				while ((line = input.readLine()) != null) {
					values.add((Double.parseDouble(line.trim())) / 100); //convert to a percentage
				}
				
				input.close();
		
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Could not find trace file '" + fileName + "'", e);
			} catch (IOException e) {
				throw new RuntimeException("Could not load trace file '" + fileName + "'", e);
			}	
			
		}
		
		public long getLastTime() {
			return stepSize  * (values.size() - 1);
		}
		
		public ArrayList<Double> getValues() {
			return values;
		}
		
		public long getStepSize() {
			return stepSize;
		}
	}

}
