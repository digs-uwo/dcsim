package edu.uwo.csd.dcsim.management;

import java.util.ArrayList;
import java.util.Collections;

import edu.uwo.csd.dcsim.DataCentre;
import edu.uwo.csd.dcsim.management.stub.HostStub;
import edu.uwo.csd.dcsim.management.stub.HostStubCpuInUseComparator;
import edu.uwo.csd.dcsim.management.stub.HostStubPowerStateComparator;
import edu.uwo.csd.dcsim.management.stub.VmStub;
import edu.uwo.csd.dcsim.management.stub.VmStubCpuInUseComparator;

public class VMRelocationPolicyST03 extends VMRelocationPolicyGreedy {

	public VMRelocationPolicyST03(DataCentre dc, double lowerThreshold, double upperThreshold, double targetUtilization) {
		super(dc, lowerThreshold, upperThreshold, targetUtilization);
	}
	
	protected ArrayList<VmStub> orderSourceVms(ArrayList<VmStub> sourceVms) {
		ArrayList<VmStub> sorted = new ArrayList<VmStub>(sourceVms);
		Collections.sort(sorted, new VmStubCpuInUseComparator());
		Collections.reverse(sorted);
		return sorted;
	}
	
	protected ArrayList<HostStub> orderTargetHosts(ArrayList<HostStub> partiallyUtilized, ArrayList<HostStub> underUtilized, ArrayList<HostStub> empty) {
		ArrayList<HostStub> targets = new ArrayList<HostStub>();
		
		//sort underutilized in descending order by CPU utilization
		Collections.sort(underUtilized, new HostStubCpuInUseComparator());
		Collections.reverse(underUtilized);
		
		//sort partiallyUtilized in increasing order by CPU utilization
		Collections.sort(partiallyUtilized, new HostStubCpuInUseComparator());
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
