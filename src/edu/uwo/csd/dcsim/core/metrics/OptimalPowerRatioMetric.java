package edu.uwo.csd.dcsim.core.metrics;

import java.util.ArrayList;
import java.util.Collections;

import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.comparator.HostComparator;

public class OptimalPowerRatioMetric extends Metric {

	private double totalCpuUsed = 0;
	private double totalPowerConsumed = 0;
	private double totalOptimalPowerConsumed = 0;
	
	private double currentCpuInUse = 0;
	private double currentPowerConsumption = 0;
	private double currentOptimalPowerConsumption = 0;
	
	public OptimalPowerRatioMetric(Simulation simulation, String name) {
		super(simulation, name);
	}

	public void update(ArrayList<Host> hosts) {
		
		for (Host host : hosts) {
			currentCpuInUse += host.getResourceManager().getCpuInUse();
			currentPowerConsumption += host.getCurrentPowerConsumption();
			
			totalCpuUsed += host.getResourceManager().getCpuInUse() * simulation.getElapsedSeconds();
			totalPowerConsumed += host.getCurrentPowerConsumption() * simulation.getElapsedSeconds();
		}
		
		currentOptimalPowerConsumption = calculateOptimalCpuPerPower(hosts);
		totalOptimalPowerConsumed += currentOptimalPowerConsumption * simulation.getElapsedSeconds();
		
	}
	
	private double calculateOptimalCpuPerPower(ArrayList<Host> hostList) {
		
		//create new list of Hosts, as we are going to resort it
		ArrayList<Host> hosts = new ArrayList<Host>(hostList);
		
		//sort hosts by power efficiency, descending
		Collections.sort(hosts, HostComparator.EFFICIENCY);
		Collections.reverse(hosts);
		
		
		/*
		 * Calculate the theoretical optimal power consumption give the current workload.
		 * 
		 * To calculate this, we first consider the total of all CPU shares currently in use in the datacentre as a single
		 * value that can be divided arbitrarily among hosts. We set 'cpuRemaining' to this value.
		 * 
		 * We then sort all Hosts in the data centre by power efficiency, starting with the most efficient host. We remove 
		 * the number of CPU shares that the most efficient host possesses from cpuRemaining, and add the power that the host
		 * would consume given 100% load to the optimal power consumption. We then move on to the next most efficient host
		 * until cpuRemaining = 0 (all cpu has been assigned to a host).
		 * 
		 * The final host will probably not be entirely filled by the cpuRemaining still left to assign. If this is the case,
		 * we calculate what the CPU utilization of the host would be given that all of cpuRemaining is placed on the host, and use
		 * this value to calculate the host's power consumption. This power consumption is then added to the optimal power consumption. 
		 * 
		 */
		double optimalPowerConsumption = 0; //the optimal total power consumption given the current load
		double cpuRemaining = currentCpuInUse; //the amount of CPU still to be allocated to a host
		
		int i = 0; //current position in host list
		while (cpuRemaining > 0) {
			
			//if there is more CPU left than available in the host
			if (cpuRemaining >= hosts.get(i).getTotalCpu()) {
				
				//remove the full capacity of the host from the remaining CPU
				cpuRemaining -= hosts.get(i).getTotalCpu();
				
				//add the host power consumption at 100% to the optimalPowerConsumption
				optimalPowerConsumption += hosts.get(i).getPowerModel().getPowerConsumption(1);
			} 
			//else if the host has enough capacity to satisfy all remaining CPU
			else {
				
				//calculate the host utilization
				double util = cpuRemaining / hosts.get(i).getTotalCpu();
				cpuRemaining = 0;
				
				//add the power consumption of the host at the calculated utilization level
				optimalPowerConsumption += hosts.get(i).getPowerModel().getPowerConsumption(util);
			}
			
			++i; //move to next host
		}
		
		return optimalPowerConsumption;
	}

	@Override
	public double getValue() {
		//return the ratio of optimal power efficiency to observed power efficiency
		double optimalEfficiency = totalCpuUsed / totalOptimalPowerConsumed;
		double observedEfficiency = totalCpuUsed / totalPowerConsumed; 
		return optimalEfficiency / observedEfficiency;
	}

	@Override
	public double getCurrentValue() {
		//return the ratio of optimal power efficiency to current power efficiency
		double optimalEfficiency = currentCpuInUse / currentOptimalPowerConsumption; 
		double currentEfficiency = currentCpuInUse / currentPowerConsumption; 
		return optimalEfficiency / currentEfficiency;
	}

	@Override
	public void onStartTimeInterval() {
		currentCpuInUse = 0;
		currentPowerConsumption = 0;
		currentOptimalPowerConsumption = 0;
	}

	@Override
	public void onCompleteTimeInterval() {
		//nothing to do
	}

	public static OptimalPowerRatioMetric getMetric(Simulation simulation, String name) {
		OptimalPowerRatioMetric metric;
		if (simulation.hasMetric(name)) {
			metric = (OptimalPowerRatioMetric)simulation.getMetric(name);
		}
		else {
			metric = new OptimalPowerRatioMetric(simulation, name);
			simulation.addMetric(metric);
		}
		return metric;	
	}

	@Override
	public String format(double value) {
		return Double.toString(getValue()); //Double.toString(Simulation.roundToMetricPrecision(getValue()));
	}
	
}
