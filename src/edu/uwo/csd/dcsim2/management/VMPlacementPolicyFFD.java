package edu.uwo.csd.dcsim2.management;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim2.host.*;
import edu.uwo.csd.dcsim2.host.comparator.*;
import edu.uwo.csd.dcsim2.vm.*;

public class VMPlacementPolicyFFD extends VMPlacementPolicy {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(VMPlacementPolicyFFD.class);
	
	@Override
	public boolean submitVM(VMAllocationRequest vmAllocationRequest) {

		ArrayList<Host> sortedHosts = sortHostList();
		Host target = findTargetHost(vmAllocationRequest, sortedHosts);

		if (target != null)
			return submitVM(vmAllocationRequest, target);

		return false;			
	}

	@Override
	public boolean submitVMs(ArrayList<VMAllocationRequest> vmAllocationRequests) {

		for (VMAllocationRequest request : vmAllocationRequests) {
			if (!submitVM(request)) {
				return false;
			}
		}
		
		return true;
	}
	
	private ArrayList<Host> sortHostList() {
		ArrayList<Host> sorted = new ArrayList<Host>();
		
		sorted.addAll(datacentre.getHosts());
		
		//orders by available allocation in increasing order (least available first) 
		Collections.sort(sorted, new HostCpuAllocationComparator());
		
		return sorted;
	}
	
	private Host findTargetHost(VMAllocationRequest vmAllocationRequest, ArrayList<Host> sortedHosts) {

		for (Host host : sortedHosts) {
			/* check allocations individually in order to override the CPU Manager's hasCapacity method to
			 * for the CPU Manager to NOT oversubscribe the initial vm placement
			 */
			if (host.isCapable(vmAllocationRequest.getVMDescription())
					&& host.getMemoryManager().hasCapacity(vmAllocationRequest)
					&& host.getBandwidthManager().hasCapacity(vmAllocationRequest)
					&& host.getStorageManager().hasCapacity(vmAllocationRequest)
					&& host.getCpuManager().getAvailableAllocation() >= vmAllocationRequest.getCpu()) {
				return host;
			}
		}
		
		return null;
	}


}
