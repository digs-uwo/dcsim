package edu.uwo.csd.dcsim.examples.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import edu.uwo.csd.dcsim.examples.management.capabilities.HostPoolManager;
import edu.uwo.csd.dcsim.examples.management.events.InstantiateVmEvent;
import edu.uwo.csd.dcsim.examples.management.events.VmPlacementEvent;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.Resources;
import edu.uwo.csd.dcsim.host.events.PowerStateEvent;
import edu.uwo.csd.dcsim.host.events.PowerStateEvent.PowerState;
import edu.uwo.csd.dcsim.management.*;
import edu.uwo.csd.dcsim.vm.VMAllocationRequest;

public class VmPlacementPolicy extends Policy {

	private double lowerThreshold;
	private double upperThreshold;
	private double targetUtilization;
	
	public VmPlacementPolicy(double lowerThreshold, double upperThreshold, double targetUtilization) {
		super(HostPoolManager.class);
		
		this.lowerThreshold = lowerThreshold;
		this.upperThreshold = upperThreshold;
		this.targetUtilization = targetUtilization;
	}
	
	private ArrayList<HostStatus> orderTargetHosts(ArrayList<HostStatus> partiallyUtilized, ArrayList<HostStatus> underUtilized, ArrayList<HostStatus> empty) {
		ArrayList<HostStatus> targets = new ArrayList<HostStatus>();
		
		// Sort Partially-utilized in increasing order by <CPU utilization,  power efficiency>.
		Collections.sort(partiallyUtilized, HostStatusComparator.getComparator(HostStatusComparator.CPU_UTIL, HostStatusComparator.EFFICIENCY));
		
		// Sort Underutilized hosts in decreasing order by <CPU utilization, power efficiency>.
		Collections.sort(underUtilized, HostStatusComparator.getComparator(HostStatusComparator.CPU_UTIL, HostStatusComparator.EFFICIENCY));
		Collections.reverse(underUtilized);
		
		// Sort Empty hosts in decreasing order by <power efficiency, power state>.
		Collections.sort(empty, HostStatusComparator.getComparator(HostStatusComparator.EFFICIENCY, HostStatusComparator.PWR_STATE));
		Collections.reverse(empty);
		
		targets.addAll(partiallyUtilized);
		targets.addAll(underUtilized);
		targets.addAll(empty);
		
		return targets;
	}
	
	private void classifyHosts(ArrayList<HostStatus> partiallyUtilized, 
			ArrayList<HostStatus> underUtilized, 
			ArrayList<HostStatus> empty,
			Map<Integer, ArrayList<HostStatus>> hosts) {
		
		for (int hostId : hosts.keySet()) {
			ArrayList<HostStatus> hostStatusList = hosts.get(hostId);
			HostStatus host = hostStatusList.get(0);
			
			// Calculate host's avg CPU utilization in the last window of time
			double avgCpuInUse = 0;
			int count = 0;
			for (HostStatus status : hostStatusList) {
				//only consider times when the host is powered on TODO should there be events from hosts that are off?
				if (status.getState() == Host.HostState.ON) {
					avgCpuInUse += status.getResourcesInUse().getCpu();
					++count;
				}
			}
			if (count != 0) {
				avgCpuInUse = avgCpuInUse / count;
			}
			
			double avgCpuUtilization = avgCpuInUse / host.getResourceCapacity().getCpu();
			
			//classify hosts, add copies of the host so that modifications can be made
			if (host.getVms().size() == 0) {
				empty.add(host.copy());
			} else if (avgCpuUtilization < lowerThreshold) {
				underUtilized.add(host.copy());
			} else if (avgCpuUtilization <= upperThreshold) {
				partiallyUtilized.add(host.copy());
			}
			
		}
		
	}
	
	public void execute(VmPlacementEvent event) {

		HostPoolManager hostPool = manager.getCapability(HostPoolManager.class);
		
		Map<Integer, ArrayList<HostStatus>> hosts = hostPool.getHostStatus();
		
		// Categorize hosts.
		ArrayList<HostStatus> partiallyUtilized = new ArrayList<HostStatus>();
		ArrayList<HostStatus> underUtilized = new ArrayList<HostStatus>();
		ArrayList<HostStatus> empty = new ArrayList<HostStatus>();
		
		this.classifyHosts(partiallyUtilized, underUtilized, empty, hosts);
		
		// Create target hosts list.
		ArrayList<HostStatus> targets = this.orderTargetHosts(partiallyUtilized, underUtilized, empty);

		for (VMAllocationRequest vmAllocationRequest : event.getVMAllocationRequests()) {
			HostStatus allocatedHost = null;
			for (HostStatus target : targets) {
				Resources reqResources = new Resources();
				reqResources.setCpu(vmAllocationRequest.getCpu());
				reqResources.setMemory(vmAllocationRequest.getMemory());
				reqResources.setBandwidth(vmAllocationRequest.getBandwidth());
				reqResources.setStorage(vmAllocationRequest.getStorage());
	
				if (target.canHost(vmAllocationRequest.getVMDescription().getCores(), vmAllocationRequest.getVMDescription().getCoreCapacity(), reqResources) &&	//target has capability and capacity to host VM
				 	(target.getResourcesInUse().getCpu() + vmAllocationRequest.getCpu()) / 
				 	target.getResourceCapacity().getCpu() <= targetUtilization) {	// target will not exceed target utilization
					 
					allocatedHost = target;
					
					//add a dummy placeholder VM to keep track of placed VM resource requirements
					target.instantiateVm(new VmStatus(vmAllocationRequest.getVMDescription().getCores(),
							vmAllocationRequest.getVMDescription().getCoreCapacity(),
							reqResources));
					
					break;
				 }
			}
			
			if (allocatedHost != null) {
				sendVM(vmAllocationRequest, allocatedHost);
			} else {
				event.addFailedRequest(vmAllocationRequest); //add a failed request to the event for any event callback listeners to check
			}
		}
	}
	
	private void sendVM(VMAllocationRequest vmAllocationRequest, HostStatus host) {
		HostPoolManager hostPool = manager.getCapability(HostPoolManager.class);
		
		//if the host is not ON or POWERING_ON, then send an event to power on the host
		if (host.getState() != Host.HostState.ON && host.getState() != Host.HostState.POWERING_ON) {
			simulation.sendEvent(new PowerStateEvent(hostPool.getHost(host.getId()), PowerState.POWER_ON));
		}
		
		//send event to host to instantiate VM
		simulation.sendEvent(new InstantiateVmEvent(hostPool.getHostManager(host.getId()), vmAllocationRequest));
	}
	
}
