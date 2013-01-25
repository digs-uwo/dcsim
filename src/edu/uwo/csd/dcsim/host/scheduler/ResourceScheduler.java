package edu.uwo.csd.dcsim.host.scheduler;

import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.vm.VMAllocation;
import edu.uwo.csd.dcsim.vm.VirtualResources;

public class ResourceScheduler {

	protected Host host;
	private ResourceSchedulerState state;
	private int remainingCpu;
	protected Simulation simulation;
	
	public enum ResourceSchedulerState {READY, COMPLETE;}
		
	/**
	 * Initialize scheduling, including resetting scheduled resources from last time interval.
	 */
	public void initScheduling() {
		
		VirtualResources scheduledResources = new VirtualResources();
		
		//reset the scheduled resources of the privileged domain
		VMAllocation privAlloc = host.getPrivDomainAllocation();
		
		scheduledResources.setCpu(0); //set CPU to 0, as this will be scheduled in rounds later
		
		//at present, all other resources are allocated their full allocation
		scheduledResources.setBandwidth(privAlloc.getBandwidth());
		scheduledResources.setMemory(privAlloc.getMemory());
		scheduledResources.setStorage(privAlloc.getStorage());
		
		privAlloc.getVm().scheduleResources(scheduledResources);
		
		//for each VM in the host, reset the resources that are current scheduled for its use
		for (VMAllocation vmAlloc : host.getVMAllocations()) {
			scheduledResources = new VirtualResources();
			
			scheduledResources.setCpu(0); //set CPU to 0, as this will be scheduled in rounds later
			
			//at present, all other resources are scheduled their full allocation
			scheduledResources.setBandwidth(vmAlloc.getBandwidth());
			scheduledResources.setMemory(vmAlloc.getMemory());
			scheduledResources.setStorage(vmAlloc.getStorage());
			
			vmAlloc.getVm().scheduleResources(scheduledResources);
		}
		
		//set the remaining CPU to the total CPU available
		remainingCpu = host.getTotalCpu();
		
		//indicate that the resource scheduler is ready to schedule resources
		state = ResourceSchedulerState.READY;
	}
	
	public void schedulePrivDomain() {
		//need to get a CPU request from priv domain
		
		//allocate all of the request
	}
	
	public void beginRound() {
		//compute shortcuts such as a round share limit for each VM
	}
	
	/**
	 * 
	 * @param vmAlloc
	 * @return true, if the VM required more resource during this scheduling round, false otherwise
	 */
	public boolean scheduleVM(VMAllocation vmAlloc) {
		//NOTE since we are running CPU as an int here, we need to make sure that no VM is starved, which really shouldn't be a problem, but think about it
		
		//get the requested and scheduled CPU from the VM
		VirtualResources requiredResources = vmAlloc.getVm().getResourcesRequired();
		VirtualResources scheduledResources = vmAlloc.getVm().getResourcesScheduled();
		
		//if the VM requires no more CPU than already scheduled, then we can return false
		if (requiredResources.getCpu() <= scheduledResources.getCpu()) {
			return false;
		}
				
		//try to give as much as we can, up to any predetermined limit for the round and no more than remaining CPU
		//TODO set round limit/shares system for CPU
		int additionalCpu = (int)Math.ceil(requiredResources.getCpu() - scheduledResources.getCpu()); //TODO should this be cast to int? Should virtual resources be int instead of double?
		
		//if there is more CPU required, and we have some left
		if (additionalCpu > 0 && remainingCpu > 0) {
			//add either the required additional cpu or the amount we have left, whichever is smaller
			int cpuToAdd = Math.min(additionalCpu, remainingCpu);

			//add the cpu to the scheduled resources of the vm
			scheduledResources.setCpu(scheduledResources.getCpu() + cpuToAdd);
			
			//allocate new resource amount
			vmAlloc.getVm().scheduleResources(scheduledResources);
		
			//subtract from remainingCpu
			remainingCpu -= cpuToAdd;
		}
		
		//if no CPU left, change the state to COMPLETE
		if (remainingCpu == 0)
			state = ResourceSchedulerState.COMPLETE;
		
		//Note that even if additionalCPU is now zero, we don't return false unless no additional CPU was scheduled during this round
	
		return true;
	}
	
	public void setHost(Host host) {
		this.host = host;
	}
	
	public ResourceSchedulerState getState() {
		return state;
	}
	
}
