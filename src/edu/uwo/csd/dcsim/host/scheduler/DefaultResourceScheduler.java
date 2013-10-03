package edu.uwo.csd.dcsim.host.scheduler;

import java.util.ArrayList;

import edu.uwo.csd.dcsim.host.Resources;
import edu.uwo.csd.dcsim.vm.*;

public class DefaultResourceScheduler extends ResourceScheduler {
	
	@Override
	public void scheduleResources() {

		Resources resourcesRemaining = new Resources (host.getResourceManager().getTotalCpu(), 
				host.getResourceManager().getTotalMemory(),
				host.getResourceManager().getTotalBandwidth(),
				host.getResourceManager().getTotalStorage()); 
		
		//first, schedule privileged domain (VMM) its full demand
		Vm privDomainVm = host.getPrivDomainAllocation().getVm();
		Resources privResourceDemand = privDomainVm.getResourceDemand();
		
		if (resourcesRemaining.getCpu() >= privResourceDemand.getCpu()) {
			resourcesRemaining.setCpu(resourcesRemaining.getCpu() - privResourceDemand.getCpu());
		} else {
			throw new RuntimeException("Host #" + host.getId() + " does not have enough CPU to execute the VMM (privileged domain)");
		}
		if (resourcesRemaining.getMemory() >= privResourceDemand.getMemory()) {
			resourcesRemaining.setMemory(resourcesRemaining.getMemory() - privResourceDemand.getMemory());
		} else {
			throw new RuntimeException("Host #" + host.getId() + " does not have enough memory to execute the VMM (privileged domain)");
		}
		if (resourcesRemaining.getBandwidth() >= privResourceDemand.getBandwidth()) {
			resourcesRemaining.setBandwidth(resourcesRemaining.getBandwidth() - privResourceDemand.getBandwidth());
		} else {
			throw new RuntimeException("Host #" + host.getId() + " does not have enough bandwidth to execute the VMM (privileged domain)");
		}
		if (resourcesRemaining.getStorage() >= privResourceDemand.getStorage()) {
			resourcesRemaining.setStorage(resourcesRemaining.getStorage() - privResourceDemand.getStorage());
		} else {
			throw new RuntimeException("Host #" + host.getId() + " does not have enough storage to execute the VMM (privileged domain)");
		}
		
		privDomainVm.scheduleResources(privResourceDemand);
		
		//build list of VM allocations that current contain a VM
		ArrayList<VmAllocation> vmAllocations = new ArrayList<VmAllocation>();
		for (VmAllocation vmAlloc : host.getVMAllocations()) {
			if (vmAlloc.getVm() != null) vmAllocations.add(vmAlloc);
		}

		//initialize resource scheduling
		for (VmAllocation vmAlloc : vmAllocations) {
			
			Vm vm = vmAlloc.getVm();
			
			//start with CPU at 0 and all other resources equal to demand
			Resources scheduled = new Resources(vm.getResourceDemand());
			scheduled.setCpu(0);
			
			//verify that enough memory, bandwidth and storage are available. For now, we simply kill the simulation if this is not the case, the the behaviour is presently undefined
			if (scheduled.getMemory() <= resourcesRemaining.getMemory()) {
				resourcesRemaining.setMemory(resourcesRemaining.getMemory() - scheduled.getMemory());
			} else {
				throw new RuntimeException("Host #" + host.getId() + " does not have enough memory to execute VM #" + vm.getId());
			}
			if (scheduled.getBandwidth() <= resourcesRemaining.getBandwidth()) {
				resourcesRemaining.setBandwidth(resourcesRemaining.getBandwidth() - scheduled.getBandwidth());
			} else {
				throw new RuntimeException("Host #" + host.getId() + " does not have enough bandwidth to execute VM #" + vm.getId());
			}
			if (scheduled.getStorage() <= resourcesRemaining.getStorage()) {
				resourcesRemaining.setStorage(resourcesRemaining.getStorage() - scheduled.getStorage());
			} else {
				throw new RuntimeException("Host #" + host.getId() + " does not have enough storage to execute VM #" + vm.getId());
			}
			
			vm.scheduleResources(scheduled);
			
		}

		//now, we schedule CPU fairly among all VMs
		int cpuShare = 0;
		int incompleteVms = vmAllocations.size();
		
		//adjust incompleteVm count to remove any VMs that have 0 CPU demand
		for (VmAllocation vmAlloc : vmAllocations) {
			if (vmAlloc.getVm().getResourceDemand().getCpu() == 0) --incompleteVms;
		}

		while (resourcesRemaining.getCpu() > 0 && incompleteVms > 0) {
			
			cpuShare = resourcesRemaining.getCpu() / incompleteVms;
			
			//if resourcesRemaining is small enough, it could be rounded to 0. Set '1' as minimum share. 
			cpuShare = Math.max(cpuShare, 1);

			for (VmAllocation vmAlloc : vmAllocations) {			
				Vm vm = vmAlloc.getVm();
				Resources scheduled = vm.getResourcesScheduled();
								
				int remainingCpuDemand = vm.getResourceDemand().getCpu() - scheduled.getCpu();

				if (remainingCpuDemand > 0) {
					if (remainingCpuDemand <= cpuShare) {
						scheduled.setCpu(scheduled.getCpu() + remainingCpuDemand);
						resourcesRemaining.setCpu(resourcesRemaining.getCoreCapacity() - remainingCpuDemand);
						--incompleteVms;
					} else {
						scheduled.setCpu(scheduled.getCpu() + cpuShare);
						resourcesRemaining.setCpu(resourcesRemaining.getCpu() - cpuShare);
					}
					
					vm.scheduleResources(scheduled);
				}
				
				//check if we are out of CPU. This can occur when share is defaulted to '1' as minimum
				if (resourcesRemaining.getCpu() == 0) break;

			}
		}
		
	}

	
}
