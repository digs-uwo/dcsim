package edu.uwo.csd.dcsim.examples.management;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.Resources;
import edu.uwo.csd.dcsim.host.events.PowerStateEvent;
import edu.uwo.csd.dcsim.host.events.PowerStateEvent.PowerState;
import edu.uwo.csd.dcsim.management.*;
import edu.uwo.csd.dcsim.management.capabilities.HostPoolManager;
import edu.uwo.csd.dcsim.management.events.InstantiateVmEvent;
import edu.uwo.csd.dcsim.management.events.ShutdownVmEvent;
import edu.uwo.csd.dcsim.management.events.VmPlacementEvent;
import edu.uwo.csd.dcsim.vm.VMAllocationRequest;

public class VmPlacementPolicy extends Policy {

	private double lowerThreshold;
	private double upperThreshold;
	private double targetUtilization;
	
	public VmPlacementPolicy(double lowerThreshold, double upperThreshold, double targetUtilization) {
		addRequiredCapability(HostPoolManager.class);
		
		this.lowerThreshold = lowerThreshold;
		this.upperThreshold = upperThreshold;
		this.targetUtilization = targetUtilization;
	}
	
	private ArrayList<HostData> orderTargetHosts(ArrayList<HostData> partiallyUtilized, 
			ArrayList<HostData> underUtilized, 
			ArrayList<HostData> empty) {
		
		ArrayList<HostData> targets = new ArrayList<HostData>();
		
		// Sort Partially-utilized in increasing order by <CPU utilization,  power efficiency>.
		Collections.sort(partiallyUtilized, HostDataComparator.getComparator(HostDataComparator.CPU_UTIL, HostDataComparator.EFFICIENCY));
		
		// Sort Underutilized hosts in decreasing order by <CPU utilization, power efficiency>.
		Collections.sort(underUtilized, HostDataComparator.getComparator(HostDataComparator.CPU_UTIL, HostDataComparator.EFFICIENCY));
		Collections.reverse(underUtilized);
		
		// Sort Empty hosts in decreasing order by <power efficiency, power state>.
		Collections.sort(empty, HostDataComparator.getComparator(HostDataComparator.EFFICIENCY, HostDataComparator.PWR_STATE));
		Collections.reverse(empty);
		
		targets.addAll(partiallyUtilized);
		targets.addAll(underUtilized);
		targets.addAll(empty);
		
		return targets;
	}
	
	private void classifyHosts(ArrayList<HostData> partiallyUtilized, 
			ArrayList<HostData> underUtilized, 
			ArrayList<HostData> empty,
			Collection<HostData> hosts) {
		
		for (HostData host : hosts) {
			
			//filter out hosts with a currently invalid status
			if (host.isStatusValid()) {
				// Calculate host's avg CPU utilization in the last window of time
				double avgCpuInUse = 0;
				int count = 0;
				for (HostStatus status : host.getHistory()) {
					//only consider times when the host is powered on TODO should there be events from hosts that are off?
					if (status.getState() == Host.HostState.ON) {
						avgCpuInUse += status.getResourcesInUse().getCpu();
						++count;
					}
				}
				if (count != 0) {
					avgCpuInUse = avgCpuInUse / count;
				}
				
				double avgCpuUtilization = avgCpuInUse / host.getHostDescription().getResourceCapacity().getCpu();
				
				//classify hosts, add copies of the host so that modifications can be made
				if (host.getCurrentStatus().getVms().size() == 0) {
					empty.add(host);
				} else if (avgCpuUtilization < lowerThreshold) {
					underUtilized.add(host);
				} else if (avgCpuUtilization <= upperThreshold) {
					partiallyUtilized.add(host);
				}
			}
			
		}
		
	}
	
	public void execute(VmPlacementEvent event) {

		HostPoolManager hostPool = manager.getCapability(HostPoolManager.class);
		
		Collection<HostData> hosts = hostPool.getHosts();
		
		//reset the sandbox host status to the current host status
		for (HostData host : hosts) {
			host.resetSandboxStatusToCurrent();
		}
		
		// Categorize hosts.
		ArrayList<HostData> partiallyUtilized = new ArrayList<HostData>();
		ArrayList<HostData> underUtilized = new ArrayList<HostData>();
		ArrayList<HostData> empty = new ArrayList<HostData>();
		
		this.classifyHosts(partiallyUtilized, underUtilized, empty, hosts);
		
		// Create target hosts list.
		ArrayList<HostData> targets = this.orderTargetHosts(partiallyUtilized, underUtilized, empty);

		for (VMAllocationRequest vmAllocationRequest : event.getVMAllocationRequests()) {
			HostData allocatedHost = null;
			for (HostData target : targets) {
				Resources reqResources = new Resources();
				reqResources.setCpu(vmAllocationRequest.getCpu());
				reqResources.setMemory(vmAllocationRequest.getMemory());
				reqResources.setBandwidth(vmAllocationRequest.getBandwidth());
				reqResources.setStorage(vmAllocationRequest.getStorage());
	
				if (HostData.canHost(vmAllocationRequest.getVMDescription().getCores(), 
						vmAllocationRequest.getVMDescription().getCoreCapacity(), 
						reqResources,
						target.getSandboxStatus(),
						target.getHostDescription()) &&	//target has capability and capacity to host VM
					 	(target.getSandboxStatus().getResourcesInUse().getCpu() + vmAllocationRequest.getCpu()) / 
					 	target.getHostDescription().getResourceCapacity().getCpu() <= targetUtilization) {	// target will not exceed target utilization
					
					allocatedHost = target;
					
					//add a dummy placeholder VM to keep track of placed VM resource requirements
					target.getSandboxStatus().instantiateVm(
							new VmStatus(vmAllocationRequest.getVMDescription().getCores(),
							vmAllocationRequest.getVMDescription().getCoreCapacity(),
							reqResources));
					
					//invalidate this host status, as we know it to be incorrect until the next status update arrives
					target.invalidateStatus(simulation.getSimulationTime());
					
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
	
	private long sendVM(VMAllocationRequest vmAllocationRequest, HostData host) {
		//if the host is not ON or POWERING_ON, then send an event to power on the host
		if (host.getCurrentStatus().getState() != Host.HostState.ON && host.getCurrentStatus().getState() != Host.HostState.POWERING_ON) {
			simulation.sendEvent(new PowerStateEvent(host.getHost(), PowerState.POWER_ON));
			
		}

		//send event to host to instantiate VM
		return simulation.sendEvent(new InstantiateVmEvent(host.getHostManager(), vmAllocationRequest));
	}
	
	public void execute(ShutdownVmEvent event) {
		HostPoolManager hostPool = manager.getCapability(HostPoolManager.class);
		AutonomicManager hostManager = hostPool.getHost(event.getHostId()).getHostManager();
		
		//mark host status as invalid
		hostPool.getHost(event.getHostId()).invalidateStatus(simulation.getSimulationTime());
		
		simulation.sendEvent(new ShutdownVmEvent(hostManager, event.getHostId(), event.getVmId()));
	}

	@Override
	public void onInstall() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onManagerStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onManagerStop() {
		// TODO Auto-generated method stub
		
	}
	
}
