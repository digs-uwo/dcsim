package edu.uwo.csd.dcsim.management.policies;

import java.util.Collection;

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
		
		Collection<HostData> hosts = hostPool.getHosts();
		
		//reset the sandbox host status to the current host status
		for (HostData host : hosts) {
			host.resetSandboxStatusToCurrent();
		}
		
		//iterate though each VM to place
		for (VMAllocationRequest vmAllocationRequest : event.getVMAllocationRequests()) {
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
