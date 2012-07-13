package edu.uwo.csd.dcsim;

import java.util.*;

import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.host.*;

public class DCUtilizationMonitor extends Monitor {

	long windowSize;
	HashMap<Host, LinkedList<Double>> utilizationValues = new HashMap<Host, LinkedList<Double>>();
	LinkedList<Double> dcUtilValues = new LinkedList<Double>();
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
	}

	@Override
	public void execute() {
		
		double dcUtil = 0;
		for (Host host : dc.getHosts()) {
			if (!utilizationValues.containsKey(host))
				utilizationValues.put(host, new LinkedList<Double>());
			
			LinkedList<Double> hostUtils = utilizationValues.get(host);
			hostUtils.addLast(host.getCpuManager().getCpuInUse());
			dcUtil += host.getCpuManager().getCpuInUse();
			
			if (hostUtils.size() > windowSize)
				hostUtils.removeFirst();
		}
		
		dcUtilValues.addLast(dcUtil);
		if (dcUtilValues.size() > windowSize)
			dcUtilValues.removeFirst();
	}
	
	public LinkedList<Double> getHostInUse(Host host) {
		return utilizationValues.get(host);
	}
	
	public LinkedList<Double> getDCInUse() {
		return dcUtilValues;
	}
	
	public long getWindowSize() {
		return windowSize;
	}

}
