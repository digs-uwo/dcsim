package edu.uwo.csd.dcsim.management.policies;

import java.util.ArrayList;
import java.util.Collection;

import edu.uwo.csd.dcsim.host.Resources;
import edu.uwo.csd.dcsim.management.*;
import edu.uwo.csd.dcsim.management.action.InstantiateVmAction;
import edu.uwo.csd.dcsim.management.capabilities.HostPoolManager;
import edu.uwo.csd.dcsim.management.events.ShutdownVmEvent;
import edu.uwo.csd.dcsim.management.events.VmPlacementEvent;
import edu.uwo.csd.dcsim.vm.VmAllocationRequest;

/**
 * DefaultVmPlacementPolicy takes a very basic approach to placement. It simply iterates through the set of hosts, in
 * no particular order (in the order they were added to the host manager), and places the VM on the first Host it
 * encounters that has enough capacity.
 * 
 * @author Michael Tighe
 *
 */
public class DefaultVmPlacementPolicy extends Policy {

	public DefaultVmPlacementPolicy() {
		addRequiredCapability(HostPoolManager.class);
	}
		
	public void execute(VmPlacementEvent event) {

		HostPoolManager hostPool = manager.getCapability(HostPoolManager.class);
		
		//filter out invalid host status
		Collection<HostData> hosts = new ArrayList<HostData>(); 
		for (HostData host : hostPool.getHosts()) {
			if (host.isStatusValid()) {
				hosts.add(host);
			}
		}
		
		//reset the sandbox host status to the current host status
		for (HostData host : hosts) {
			host.resetSandboxStatusToCurrent();
		}
		
		//iterate though each VM to place
		for (VmAllocationRequest vmAllocationRequest : event.getVMAllocationRequests()) {
			HostData allocatedHost = null;
			
			//simply iterate through the list of hosts until we find one that has enough capacity for the VM
			for (HostData target : hosts) {
				Resources reqResources = new Resources();
				reqResources.setCpu(vmAllocationRequest.getCpu());
				reqResources.setMemory(vmAllocationRequest.getMemory());
				reqResources.setBandwidth(vmAllocationRequest.getBandwidth());
				reqResources.setStorage(vmAllocationRequest.getStorage());

				if (HostData.canHost(vmAllocationRequest.getVMDescription().getCores(), 
						vmAllocationRequest.getVMDescription().getCoreCapacity(), 
						reqResources,
						target.getSandboxStatus(),
						target.getHostDescription())) {	//target has capability and capacity to host VM
					
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
				InstantiateVmAction instantiateVmAction = new InstantiateVmAction(allocatedHost, vmAllocationRequest, event);
				instantiateVmAction.execute(simulation, this);
			} else {
				event.addFailedRequest(vmAllocationRequest); //add a failed request to the event for any event callback listeners to check
			}
		}
	}
	
	public void execute(ShutdownVmEvent event) {
		HostPoolManager hostPool = manager.getCapability(HostPoolManager.class);
		AutonomicManager hostManager = hostPool.getHost(event.getHostId()).getHostManager();
		
		//mark host status as invalid
		hostPool.getHost(event.getHostId()).invalidateStatus(simulation.getSimulationTime());
		
		//prevent the original event from logging, since we are creating a new event to forward to the host
		event.setLog(false);
		
		ShutdownVmEvent shutdownEvent = new ShutdownVmEvent(hostManager, event.getHostId(), event.getVmId()); 
		event.addEventInSequence(shutdownEvent);
		simulation.sendEvent(shutdownEvent);
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
