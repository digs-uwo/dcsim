package edu.uwo.csd.dcsim.host.scheduler;

import edu.uwo.csd.dcsim.vm.VMAllocation;
import edu.uwo.csd.dcsim.vm.VirtualResources;

public class DefaultResourceScheduler extends ResourceScheduler {

	private int roundCpuShare;
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
		//need to get a CPU request from priv domain
		
		//allocate all of the request
	}
	
	public void beginRound() {
		roundCpuShare = getRemainingCpu() / nVms;
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
		VirtualResources requiredResources = vmAlloc.getVm().getResourcesRequired();
		VirtualResources scheduledResources = vmAlloc.getVm().getResourcesScheduled();
		
		//if the VM requires no more CPU than already scheduled, then we can return false
		if (requiredResources.getCpu() <= scheduledResources.getCpu()) {
			--nVms;
			return false;
		}
				
		//try to give as much as we can, up to any predetermined limit for the round and no more than remaining CPU
		int additionalCpu = (int)Math.ceil(requiredResources.getCpu() - scheduledResources.getCpu()); //TODO should this be cast to int? Should virtual resources be int instead of double?
		
		//cap additionalCpu at the round share
		additionalCpu = Math.min(additionalCpu, roundCpuShare);
		
		//if there is more CPU required, and we have some left
		if (additionalCpu > 0 && getRemainingCpu() > 0) {
			//add either the required additional cpu or the amount we have left, whichever is smaller
			int cpuToAdd = Math.min(additionalCpu, getRemainingCpu());

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
