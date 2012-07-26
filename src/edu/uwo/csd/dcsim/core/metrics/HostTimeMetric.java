package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.core.Simulation;

public class HostTimeMetric extends AggregateMetric {

	public HostTimeMetric(Simulation simulation, String name) {
		super(simulation, name);
	}
	
	@Override
	public String toString() {
		return Double.toString(Simulation.roundToMetricPrecision(getValue() / 3600)) + "hrs"; //convert from seconds to hours
	}
	
	public static HostTimeMetric getSimulationMetric(Simulation simulation, String name) {
		HostTimeMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (HostTimeMetric)simulation.getMetric(name);
		}
		else {
			metric = new HostTimeMetric(simulation, name);
			simulation.addMetric(metric);
		}
		return metric;	
	}

}
