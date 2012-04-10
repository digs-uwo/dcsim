package edu.uwo.csd.dcsim2.management;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim2.DataCentre;
import edu.uwo.csd.dcsim2.core.Event;
import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.host.*;
import edu.uwo.csd.dcsim2.host.comparator.HostCpuUtilizationComparator;
import edu.uwo.csd.dcsim2.host.comparator.HostPowerStateComparator;
import edu.uwo.csd.dcsim2.vm.*;

public class VMRelocationPolicyST03 extends VMRelocationPolicyGreedy {

	public VMRelocationPolicyST03(DataCentre dc, long interval, long firstEvent, double lowerThreshold, double upperThreshold, double targetUtilization) {
		super(dc, interval, firstEvent, lowerThreshold, upperThreshold, targetUtilization);
	}
	
	protected ArrayList<VmStub> orderSourceVms(ArrayList<VmStub> sourceVms) {
		ArrayList<VmStub> sorted = new ArrayList<VmStub>(sourceVms);
		Collections.sort(sorted, new VmStubCpuUtilizationComparator());
		Collections.reverse(sorted);
		return sorted;
	}
	
	protected ArrayList<HostStub> orderTargetHosts(ArrayList<HostStub> partiallyUtilized, ArrayList<HostStub> underUtilized, ArrayList<HostStub> empty) {
		ArrayList<HostStub> targets = new ArrayList<HostStub>();
		
		//sort underutilized in descending order by CPU utilization
		Collections.sort(underUtilized, new HostStubCpuUtilizationComparator());
		Collections.reverse(underUtilized);
		
		//sort partiallyUtilized in increasing order by CPU utilization
		Collections.sort(partiallyUtilized, new HostStubCpuUtilizationComparator());
		//Collections.reverse(partiallyUtilized);
		
		//sort empty by power state (on, off, suspended)
		Collections.sort(empty, new HostStubPowerStateComparator());
		Collections.reverse(empty);
		
		targets.addAll(partiallyUtilized);
		targets.addAll(underUtilized);
		targets.addAll(empty);
		
		return targets;
	}

}
