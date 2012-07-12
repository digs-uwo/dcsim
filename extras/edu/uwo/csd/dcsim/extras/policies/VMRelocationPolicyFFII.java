/*
 * Title:        DCSim Toolkit
 * Description:  DCSim (Data Centre Simulator) Toolkit for Modeling and Simulation of Data Centres
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 */
package edu.uwo.csd.dcsim.extras.policies;

import java.util.ArrayList;
import java.util.Collections;

import edu.uwo.csd.dcsim.*;
import edu.uwo.csd.dcsim.management.VMRelocationPolicyGreedy;
import edu.uwo.csd.dcsim.management.stub.*;

/**
 * VmRelocationPolicyFFII implements the following VM relocation policy:
 * 
 * - relocation candidates: VMs with less CPU load than the CPU load by which 
 *   the host is stressed are ignored. The rest of the VMs are sorted in 
 *   increasing order by CPU load;
 * - target hosts: sort Partially-utilized and Underutilized hosts in 
 *   increasing order by CPU utilization, and Empty hosts in decreasing order 
 *   by power state. Return the hosts in the following order: Underutilized, 
 *   Partially-utilized and Empty.
 * 
 * @author gkeller2
 *
 */
public class VMRelocationPolicyFFII extends VMRelocationPolicyGreedy {

	/**
	 * Creates a new instance of VMRelocationPolicyFFII.
	 */
	public VMRelocationPolicyFFII(DataCentre dc, DCUtilizationMonitor utilizationMonitor, double lowerThreshold, double upperThreshold, double targetUtilization) {
		super(dc, utilizationMonitor, lowerThreshold, upperThreshold, targetUtilization);
	}
	
	/**
	 * Sorts the relocation candidates in increasing order by CPU load, 
	 * previously removing from consideration those VMs with less CPU load 
	 * than the CPU load by which the host is stressed.
	 */
	@Override
	protected ArrayList<VmStub> orderSourceVms(ArrayList<VmStub> sourceVms) {
		ArrayList<VmStub> sorted = new ArrayList<VmStub>();
		
		// Remove VMs with less CPU load than the CPU load by which the source 
		// host is stressed.
		HostStub source = sourceVms.get(0).getHost();
		double cpuExcess = source.getCpuInUse() - source.getTotalCpu() * this.upperThreshold;
		for (VmStub vm : sourceVms)
			if (vm.getCpuInUse() >= cpuExcess)
				sorted.add(vm);
		
		if (!sorted.isEmpty())
			// Sort VMs in increasing order by CPU load.
			Collections.sort(sorted, new VmStubCpuInUseComparator());
		else {
			// Add original list of VMs and sort them in decreasing order by 
			// CPU load, so as to avoid trying to migrate the smallest VMs 
			// first (which would not help resolve the stress situation).
			sorted.addAll(sourceVms);
			Collections.sort(sorted, new VmStubCpuInUseComparator());
			Collections.reverse(sorted);
		}
		
		return sorted;
	}
	
	/**
	 * Sorts Partially-utilized and Underutilized hosts in increasing order by 
	 * CPU utilization, and Empty hosts in decreasing order by power state. 
	 * Returns the target hosts in the following order: Underutilized, 
	 * Partially-utilized and Empty.
	 */
	@Override
	protected ArrayList<HostStub> orderTargetHosts(
			ArrayList<HostStub> partiallyUtilized,
			ArrayList<HostStub> underUtilized, ArrayList<HostStub> empty) {
		
		ArrayList<HostStub> targets = new ArrayList<HostStub>();
		
		// Sort Partially-utilized hosts in increasing order by CPU utilization.
		Collections.sort(partiallyUtilized, new HostStubCpuInUseComparator());
		//Collections.reverse(partiallyUtilized);
		
		// Sort Underutilized hosts in increasing order by CPU utilization.
		Collections.sort(underUtilized, new HostStubCpuInUseComparator());
		//Collections.reverse(underUtilized);
		
		// Sort Empty hosts in decreasing order by power state 
		// (on, suspended, off).
		Collections.sort(empty, new HostStubPowerStateComparator());
		Collections.reverse(empty);
		
		targets.addAll(underUtilized);
		targets.addAll(partiallyUtilized);
		targets.addAll(empty);
		
		return targets;
	}

}
