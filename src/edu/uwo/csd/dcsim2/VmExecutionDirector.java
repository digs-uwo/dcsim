package edu.uwo.csd.dcsim2;

import java.util.*;

import edu.uwo.csd.dcsim2.vm.*;
import edu.uwo.csd.dcsim2.host.Host;
import edu.uwo.csd.dcsim2.host.scheduler.CpuScheduler;
import edu.uwo.csd.dcsim2.host.scheduler.VMAllocationSchedulingComparator;

public final class VmExecutionDirector {
		
	private ArrayList<VMAllocation> buildVmList(ArrayList<Host> hosts) {
		ArrayList<VMAllocation> vmList = new ArrayList<VMAllocation>();
		
		for (Host host : hosts) {
			vmList.addAll(host.getVMAllocations());
		}
		Collections.sort(vmList, new VMAllocationSchedulingComparator());
		
		return vmList;
	}
	
	public void execute(ArrayList<Host> hosts) {

		//retrieve ordered list of vm allocations
		ArrayList<VMAllocation> vmList = buildVmList(hosts);
		
		//calculate the resources each VM has available
		for (VMAllocation vmAllocation : vmList) {
			if (vmAllocation.getVm() != null)
				vmAllocation.getVm().prepareExecution();
		}
		
		for (Host host : hosts) {
			if (host.getState() == Host.HostState.ON) {
				host.getCpuScheduler().prepareScheduler();
				host.getCpuScheduler().beginScheduling();
				
				host.getPrivDomainAllocation().getVm().prepareExecution();
				host.getCpuScheduler().schedulePrivDomain(host.getPrivDomainAllocation());
			}
		}
		
		HashSet<VMAllocation> completedVms = new HashSet<VMAllocation>();
		boolean notDone = true;
		do {
			notDone = false;
			
			for (Host host : hosts) {
				if (host.getState() == Host.HostState.ON)
					host.getCpuScheduler().beginRound();
			}
			
			//execute VMs
			for (VMAllocation vmAllocation : vmList) {
				if (!completedVms.contains(vmAllocation) && vmAllocation.getVm() != null && vmAllocation.getHost().getState() == Host.HostState.ON) { //ensure that a VM is instantiated within the allocation					
					if (vmAllocation.getHost().getCpuScheduler().getState() != CpuScheduler.CpuSchedulerState.COMPLETE) {
						if (vmAllocation.getHost().getCpuScheduler().processVM(vmAllocation)) {
							notDone = true;
						} else {
							completedVms.add(vmAllocation);
						}
					}
				}
			}
			
			for (Host host : hosts) {
				if (host.getState() == Host.HostState.ON)
					host.getCpuScheduler().endRound();
			}
			
		} while (notDone);
		
		for (Host host : hosts) {
			if (host.getState() == Host.HostState.ON) {
				host.getCpuScheduler().endScheduling();
				
				host.getPrivDomainAllocation().getVm().completeExecution();
			}
		}
		
		//update the resourcesInUse for each VM
		for (VMAllocation vmAllocation : vmList) {
			if (vmAllocation.getVm() != null) {		
				vmAllocation.getVm().completeExecution();
			}
		}

	}
}
