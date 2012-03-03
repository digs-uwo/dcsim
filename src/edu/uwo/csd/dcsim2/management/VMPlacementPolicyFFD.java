package edu.uwo.csd.dcsim2.management;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.host.*;
import edu.uwo.csd.dcsim2.host.comparator.*;
import edu.uwo.csd.dcsim2.vm.*;

public class VMPlacementPolicyFFD extends VMPlacementPolicy {

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
		ArrayList<Host> sortedHosts = sortHostList();
		
		for (VMAllocationRequest request : vmAllocationRequests) {
			Host target = findTargetHost(request, sortedHosts);
			if (target != null) {
				submitVM(request, target);
			} else {
				return false; //fail as soon as one vm cannot be allocated... is this correct?
			}
		}
		
		return true;
	}
	
	@Override
	public boolean submitVM(VMAllocationRequest vmAllocationRequest, Host host) {

		if (host.hasCapacity(vmAllocationRequest)) {
			sendVM(vmAllocationRequest, host);
			return true;
		} else {
			return false;
		}
		
	}
	
	private void sendVM(VMAllocationRequest vmAllocationRequest, Host host) {
		
		if (host.getState() != Host.HostState.ON && host.getState() != Host.HostState.POWERING_ON) {
			Simulation.getSimulation().sendEvent(
					new Event(Host.HOST_POWER_ON_EVENT,
							Simulation.getSimulation().getSimulationTime(),
							this,
							host)
					);
		}
		
		host.submitVM(vmAllocationRequest);
		logger.info("Submitted VM to Host #" + host.getId());
	}

	private ArrayList<Host> sortHostList() {
		ArrayList<Host> sorted = new ArrayList<Host>();
		
		sorted.addAll(datacentre.getHosts());
		Collections.sort(sorted, new HostCpuUtilizationComparator());
		Collections.reverse(sorted); //switch to decreasing order
		return sorted;
	}
	
	private Host findTargetHost(VMAllocationRequest vmAllocationRequest, ArrayList<Host> sortedHosts) {

		for (Host host : sortedHosts) {
			if (host.hasCapacity(vmAllocationRequest)) {
				return host;
			}
		}
		
		return null;
	}


}
