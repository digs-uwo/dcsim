package edu.uwo.csd.dcsim.host.scheduler;

import edu.uwo.csd.dcsim.host.Resources;
import edu.uwo.csd.dcsim.vm.VMAllocation;

public class DefaultResourceScheduler extends ResourceScheduler {

	private double roundCpuShare;
	private int nVms; 
	
	public enum ResourceSchedulerState {READY, COMPLETE;}
		
	/**
	 * Initialize scheduling, including resetting scheduled resources from last time interval.
	 */
	@Override
	public void beginScheduling() {
		//set the number of VMs to schedule
		nVms = host.getVMAllocations().size();
	}
	
	public void schedulePrivDomain() {
		//allocate all of the required cpu
		Resources requiredResources = host.getPrivDomainAllocation().getVm().getResourcesRequired();
		Resources scheduledResources = host.getPrivDomainAllocation().getVm().getResourcesScheduled();
		
		int requiredCpu = (int)Math.ceil(requiredResources.getCpu());
		
		//we always want to schedule the priv domain all it requires first, and there should be sufficient CPU to handle it. If not, kill the simulation. 
		if (requiredCpu > getRemainingCpu()) {
			throw new RuntimeException("Insufficient resources to run privileged domain on host #" + host.getId());
		}
		
		scheduledResources.setCpu(requiredCpu);
		
		host.getPrivDomainAllocation().getVm().scheduleResources(scheduledResources);
		
		scheduleCpu(requiredCpu);
	}
	
	public void beginRound() {
		if (nVms > 1) {
			roundCpuShare = getRemainingCpu() / nVms;
			
			//put a lower limit on the round share to avoid scheduling very small amounts
			if (roundCpuShare < 1)
				roundCpuShare = 1;
			
		} else {
			roundCpuShare = 0; //irrelevant value, as there are no VMs which will execute
		}
	}
	
	/**
	 * 
	 * @param vmAlloc
	 * @return true, if the VM required more resource during this scheduling round, false otherwise
	 */
	@Override
	public boolean scheduleVM(VMAllocation vmAlloc) {
		//NOTE since we are running CPU as an int here, we need to make sure that no VM is starved, which really shouldn't be a problem, but think about it
		
		//get the requested and scheduled CPU from the VM
		Resources requiredResources = vmAlloc.getVm().getResourcesRequired();
		Resources scheduledResources = vmAlloc.getVm().getResourcesScheduled();
		
		//if the VM requires no more CPU than already scheduled, then we can return false
		if (requiredResources.getCpu() <= scheduledResources.getCpu()) {
			--nVms;
			return false;
		}
				
		//try to give as much as we can, up to any predetermined limit for the round and no more than remaining CPU
		double additionalCpu = requiredResources.getCpu() - scheduledResources.getCpu();
		
		//cap additionalCpu at the round share
		additionalCpu = Math.min(additionalCpu, roundCpuShare);
		
		//if there is more CPU required, and we have some left
		if (additionalCpu > 0 && getRemainingCpu() > 0) {
			//add either the required additional cpu or the amount we have left, whichever is smaller
			double cpuToAdd = Math.min(additionalCpu, getRemainingCpu());

			//add the cpu to the scheduled resources of the vm
			scheduledResources.setCpu(scheduledResources.getCpu() + cpuToAdd);
			
			//allocate new resource amount
			vmAlloc.getVm().scheduleResources(scheduledResources);
		
			//subtract from remainingCpu
			scheduleCpu(cpuToAdd);
		}
		
		//Note that even if additionalCPU is now zero, we don't return false unless no additional CPU was scheduled during this round	
		return true;
	}

	
}
