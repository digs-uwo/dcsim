package edu.uwo.csd.dcsim.core.metrics;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim.application.Application;
import edu.uwo.csd.dcsim.common.Tuple;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.host.Cluster;
import edu.uwo.csd.dcsim.host.Host;

public abstract class MetricCollection {

	protected Simulation simulation;
	
	public MetricCollection(Simulation simulation) {
		this.simulation = simulation;
	}
	
	public abstract void completeSimulation();
	
	public abstract void printDefault(Logger out);
	
	public abstract List<Tuple<String, Object>> getMetricValues();
	
	public void recordMetrics() {
		//DO NOTHING - Override if desired
		//TODO: change this to abstract, use to replace other three
	}
	
	public void recordApplicationMetrics(Collection<Application> applications) {
		//DO NOTHING - Override if desired 
	}
	
	public void recordHostMetrics(Collection<Host> hosts) {
		//DO NOTHING - Override if desired
	}
	
	public void recordClusterMetrics(Collection<Cluster> clusters) {
		//DO NOTHING - Override if desired
	}
	
}
