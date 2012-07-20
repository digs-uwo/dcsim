package edu.uwo.csd.dcsim;

import java.util.*;

import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.host.*;
import edu.uwo.csd.dcsim.vm.VMAllocation;

public class DCUtilizationMonitor extends Monitor {

	long windowSize;
	HashMap<Host, LinkedList<Double>> utilizationValues = new HashMap<Host, LinkedList<Double>>();
	LinkedList<Double> dcUtilValues = new LinkedList<Double>();
	LinkedList<Double> dcSLAValues = new LinkedList<Double>();
	LinkedList<Double> dcPowerValues = new LinkedList<Double>();
	double totalSlavWork;
	double totalWork;
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
		double slavWork = 0;
		double work = 0;		
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
				slavWork += vmAlloc.getVm().getApplication().getSLAViolatedWork();
				work += vmAlloc.getVm().getApplication().getIncomingWork(); //NOTE: This ONLY works with SINGLE TIERED applications. For multi-tiered applications, this will count incoming work multiple times!!
				
				totalSlavWork += vmAlloc.getVm().getApplication().getTotalSLAViolatedWork();
				totalWork += vmAlloc.getVm().getApplication().getTotalIncomingWork();
			}
			
			//get power consumption
			dcPower += host.getCurrentPowerConsumption();
			totalPower += host.getPowerConsumed();
		}
		
		dcUtilValues.addFirst(dcUtil);
		dcPowerValues.addFirst(dcPower);
		dcSLAValues.addFirst(slavWork / work);
		if (dcUtilValues.size() > windowSize) {
			dcUtilValues.removeLast();
			dcPowerValues.removeLast();
			dcSLAValues.removeLast();
		}
			
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
