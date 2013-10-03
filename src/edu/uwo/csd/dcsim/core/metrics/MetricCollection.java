package edu.uwo.csd.dcsim.core.metrics;

import java.util.List;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim.common.Tuple;
import edu.uwo.csd.dcsim.core.Simulation;

public abstract class MetricCollection {

	Simulation simulation;
	
	public MetricCollection(Simulation simulation) {
		this.simulation = simulation;
	}
	
	public abstract void completeSimulation();
	
	public abstract void printDefault(Logger out);
	
	public abstract List<Tuple<String, Object>> getMetricValues();
}
