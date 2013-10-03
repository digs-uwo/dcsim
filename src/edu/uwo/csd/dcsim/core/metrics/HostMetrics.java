package edu.uwo.csd.dcsim.core.metrics;

import java.util.*;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim.common.Tuple;
import edu.uwo.csd.dcsim.common.Utility;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.host.Host;

public class HostMetrics extends MetricCollection {

	WeightedMetric powerConsumption = new WeightedMetric();
	WeightedMetric powerEfficiency = new WeightedMetric();
	
	WeightedMetric activeHosts = new WeightedMetric();
	WeightedMetric hostUtilization = new WeightedMetric();
	WeightedMetric totalUtilization = new WeightedMetric();
	
	long nHosts;
	
	public HostMetrics(Simulation simulation) {
		super(simulation);
	}
	
	public void recordHostMetrics(Collection<Host> hosts) {
		double currentPowerConsumption = 0;
		double currentActiveHosts = 0;
		double currentTotalInUse = 0;
		double currentTotalCapacity = 0;
		double currentTotalUtilization;
		
		nHosts = hosts.size();
		
		for (Host host : hosts) {
			currentPowerConsumption += host.getCurrentPowerConsumption();
			
			if (host.getState() == Host.HostState.ON) {
				++currentActiveHosts;
				hostUtilization.add(host.getResourceManager().getCpuUtilization(), simulation.getElapsedTime());
			}
			
			currentTotalInUse += host.getResourceManager().getCpuInUse();
			currentTotalCapacity += host.getResourceManager().getTotalCpu();
		}
			
		currentTotalUtilization = currentTotalInUse / currentTotalCapacity;
		
		powerConsumption.add(currentPowerConsumption, simulation.getElapsedSeconds());
		powerEfficiency.add(currentTotalInUse / currentPowerConsumption, simulation.getElapsedSeconds());
		activeHosts.add(currentActiveHosts, simulation.getElapsedTime());
		totalUtilization.add(currentTotalUtilization, simulation.getElapsedTime());
		
	}
	
	public WeightedMetric getPowerConsumption() {
		return powerConsumption;
	}
	
	public WeightedMetric getPowerEfficiency() {
		return powerEfficiency;
	}
	
	public WeightedMetric getActiveHosts() {
		return activeHosts;
	}
	
	public WeightedMetric getHostUtilization() {
		return hostUtilization;
	}
	
	public WeightedMetric getTotalUtilization() {
		return totalUtilization;
	}

	@Override
	public void completeSimulation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printDefault(Logger out) {
		out.info("-- HOSTS --");
		out.info("   nHosts: " + nHosts);
		out.info("Active Hosts");
		out.info("   max: " + Utility.roundDouble(getActiveHosts().getMax(), Simulation.getMetricPrecision()));
		out.info("   mean: " + Utility.roundDouble(getActiveHosts().getMean(), Simulation.getMetricPrecision()));
		out.info("   min: " + Utility.roundDouble(getActiveHosts().getMin(), Simulation.getMetricPrecision()));
		out.info("   util: " + Utility.roundDouble(Utility.toPercentage(getHostUtilization().getMean()), Simulation.getMetricPrecision()) + "%");
		out.info("   total util: " + Utility.roundDouble(Utility.toPercentage(getTotalUtilization().getMean()), Simulation.getMetricPrecision()) + "%");
		
		out.info("Power");
		out.info("   consumed: " + Utility.roundDouble(Utility.toKWH(getPowerConsumption().getSum()), Simulation.getMetricPrecision()) + "kWh");
		out.info("   max: " + Utility.roundDouble(getPowerConsumption().getMax(), Simulation.getMetricPrecision()) + "Ws");
		out.info("   mean: " + Utility.roundDouble(getPowerConsumption().getMean(), Simulation.getMetricPrecision()) + "Ws");
		out.info("   min: " + Utility.roundDouble(getPowerConsumption().getMin(), Simulation.getMetricPrecision()) + "Ws");
		out.info("   efficiency: " + Utility.roundDouble(getPowerEfficiency().getMean(), Simulation.getMetricPrecision()) + "cpu/watt");
		
	}

	@Override
	public List<Tuple<String, Object>> getMetricValues() {
		List<Tuple<String, Object>> metrics = new ArrayList<Tuple<String, Object>>();
		
		metrics.add(new Tuple<String, Object>("nHosts", nHosts));
		
		metrics.add(new Tuple<String, Object>("activeHostsMax", Utility.roundDouble(getActiveHosts().getMax(), Simulation.getMetricPrecision())));
		metrics.add(new Tuple<String, Object>("activeHostsMean", Utility.roundDouble(getActiveHosts().getMean(), Simulation.getMetricPrecision())));
		metrics.add(new Tuple<String, Object>("activeHostsMin", Utility.roundDouble(getActiveHosts().getMin(), Simulation.getMetricPrecision())));
		metrics.add(new Tuple<String, Object>("activeHostsUtil", Utility.roundDouble(Utility.toPercentage(getHostUtilization().getMean()), Simulation.getMetricPrecision())));
		metrics.add(new Tuple<String, Object>("totalUtil", Utility.roundDouble(Utility.toPercentage(getTotalUtilization().getMean()), Simulation.getMetricPrecision())));
		
		metrics.add(new Tuple<String, Object>("powerConsumed", Utility.roundDouble(Utility.toKWH(getPowerConsumption().getSum()), Simulation.getMetricPrecision())));
		metrics.add(new Tuple<String, Object>("powerMax", Utility.roundDouble(getPowerConsumption().getMax(), Simulation.getMetricPrecision())));
		metrics.add(new Tuple<String, Object>("powerMean", Utility.roundDouble(getPowerConsumption().getMean(), Simulation.getMetricPrecision())));
		metrics.add(new Tuple<String, Object>("powerMin", Utility.roundDouble(getPowerConsumption().getMin(), Simulation.getMetricPrecision())));
		metrics.add(new Tuple<String, Object>("powerEfficiency", Utility.roundDouble(getPowerEfficiency().getMean(), Simulation.getMetricPrecision())));
		
		return metrics;
	}
}
