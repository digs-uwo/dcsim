package edu.uwo.csd.dcsim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import edu.uwo.csd.dcsim.core.Monitor;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.comparator.HostComparator;
import edu.uwo.csd.dcsim.vm.VMAllocation;

public class DCUtilizationMonitor extends Monitor {

	long windowSize;
	HashMap<Host, LinkedList<Double>> utilizationValues = new HashMap<Host, LinkedList<Double>>();
	LinkedList<Double> dcUtilValues = new LinkedList<Double>();
	LinkedList<Double> dcSLAValues = new LinkedList<Double>();
	LinkedList<Double> dcPowerValues = new LinkedList<Double>();
	LinkedList<Double> dcOptimalPowerValues = new LinkedList<Double>();
	LinkedList<Double> dcOptimalPowerRatioValues = new LinkedList<Double>();
	
	double totalSlavWork = 0;
	double totalWork = 0;
	
	double totalPower;
	DataCentre dc;
	
	/**
	 * Construct a new DCUtilizationMonitor
	 * @param simulation
	 * @param frequency The frequency in milliseconds to run this monitor
	 * @param windowSize The number of historical values to use in calculations
	 * @param dc
	 */
	public DCUtilizationMonitor(Simulation simulation, long frequency, long windowSize, DataCentre dc) {
		super(simulation, frequency);
		this.windowSize = windowSize;
		this.dc = dc;
		
		//initialize host values
		for (Host host : dc.getHosts()) {
			utilizationValues.put(host, new LinkedList<Double>());
		}
	}

	@Override
	public void execute() {
		
		double dcUtil = 0;
		double dcPower = 0;
		double prevSlavWork;
		double prevWork;		
		
		//store current work and SLA violated work values
		prevSlavWork = totalSlavWork;
		prevWork = totalWork;
		
		//reset total work values
		totalSlavWork = 0;
		totalWork = 0;
		
		totalPower = 0;
		
		for (Host host : dc.getHosts()) {
			
			//store host CPU utilization
			if (!utilizationValues.containsKey(host))
				utilizationValues.put(host, new LinkedList<Double>());
			
			LinkedList<Double> hostUtils = utilizationValues.get(host);
			hostUtils.addLast(host.getCpuManager().getCpuInUse());
			dcUtil += host.getCpuManager().getCpuInUse();
			
			if (hostUtils.size() > windowSize)
				hostUtils.removeFirst();
			
			//get VM SLA values
			for (VMAllocation vmAlloc : host.getVMAllocations()) {							
				totalSlavWork += vmAlloc.getVm().getApplication().getTotalSLAViolatedWork();
				totalWork += vmAlloc.getVm().getApplication().getTotalIncomingWork(); //NOTE: This ONLY works with SINGLE TIERED applications. For multi-tiered applications, this will count incoming work multiple times!!
			}
			
			//get power consumption
			dcPower += host.getCurrentPowerConsumption();
			totalPower += host.getPowerConsumed();
		}
		
		dcUtilValues.addFirst(dcUtil);
		
		dcPowerValues.addFirst(dcPower);
		double optimalPowerEfficiency = calculateOptimalPowerEfficiency(dcUtil);  
		dcOptimalPowerValues.addFirst(optimalPowerEfficiency);
		dcOptimalPowerRatioValues.addFirst(optimalPowerEfficiency / (dcUtil / dcPower));
		
		//records the total fraction of SLA violated incoming work since the last time interval
		dcSLAValues.addFirst((totalSlavWork - prevSlavWork) / (totalWork - prevWork));
		
		if (dcUtilValues.size() > windowSize) {
			dcUtilValues.removeLast();
			dcPowerValues.removeLast();
			dcSLAValues.removeLast();
			dcOptimalPowerValues.removeLast();
			dcOptimalPowerRatioValues.removeLast();
		}
			
	}
	
	private double calculateOptimalPowerEfficiency(double cpuInUse) {
		
		//create new list of all of the Hosts in the datacentre. We create a new list as we are going to resort it
		ArrayList<Host> hosts = new ArrayList<Host>(dc.getHosts());
		
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
		double cpuRemaining = cpuInUse; //the amount of CPU still to be allocated to a host
		
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
		
		//optimal cpu-per-power is (current CPU in use / optimal power consumption)
		return cpuInUse / optimalPowerConsumption;
	}
	
	public LinkedList<Double> getHostInUse(Host host) {
		return utilizationValues.get(host);
	}
	
	public LinkedList<Double> getDCInUse() {
		return dcUtilValues;
	}
	
	public LinkedList<Double> getDCsla() {
		return dcSLAValues;
	}
	
	public LinkedList<Double> getDCPower() {
		return dcPowerValues;
	}
	
	public LinkedList<Double> getDCOptimalPower() {
		return dcOptimalPowerValues;
	}
	
	public LinkedList<Double> getDCOptimalPowerRatio() {
		return dcOptimalPowerRatioValues;
	}
	
	public long getWindowSize() {
		return windowSize;
	}
	
	public double getTotalSlavWork() {
		return totalSlavWork;
	}
	
	public double getTotalWork() {
		return totalWork;
	}
	
	public double getTotalPower() {
		return totalPower;
	}

}
