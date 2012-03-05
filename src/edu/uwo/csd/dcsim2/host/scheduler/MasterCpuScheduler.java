package edu.uwo.csd.dcsim2.host.scheduler;

import java.util.*;

import edu.uwo.csd.dcsim2.vm.*;
import edu.uwo.csd.dcsim2.host.Host;

public class MasterCpuScheduler {

	private static MasterCpuScheduler masterCpuScheduler = new MasterCpuScheduler();
	
	private ArrayList<CpuScheduler> cpuSchedulers;

	public static MasterCpuScheduler getMasterCpuScheduler() {
		return masterCpuScheduler;
	}
	
	private MasterCpuScheduler() {
		cpuSchedulers = new ArrayList<CpuScheduler>();	
	}
	
	public ArrayList<CpuScheduler> getCpuSchedulers() {
		return cpuSchedulers;
	}
	
	private ArrayList<VMAllocation> buildVmList() {
		ArrayList<VMAllocation> vmList = new ArrayList<VMAllocation>();
		
		for (CpuScheduler cpuScheduler : cpuSchedulers) {
			if (cpuScheduler.getHost().getState() == Host.HostState.ON)
				vmList.addAll(cpuScheduler.getHost().getVMAllocations());
		}
		Collections.sort(vmList, new VMAllocationSchedulingComparator());
		
		return vmList;
	}
	
	public void scheduleCpu() {

		//retrieve ordered list of vm allocations
		ArrayList<VMAllocation> vmList = buildVmList();
		
		//calculate the resources each VM has available
		for (VMAllocation vmAllocation : vmList) {
			vmAllocation.getVm().beginScheduling();
		}
		
		for (CpuScheduler cpuScheduler : cpuSchedulers) {
			if (cpuScheduler.getHost().getState() == Host.HostState.ON) {
				cpuScheduler.prepareScheduler();
				cpuScheduler.beginScheduling();
				
				cpuScheduler.getHost().getPrivDomainAllocation().getVm().beginScheduling();
				cpuScheduler.schedulePrivDomain(cpuScheduler.getHost().getPrivDomainAllocation());
			}
		}
		
		boolean notDone = true;
		do {
			notDone = false;
			
			for (CpuScheduler cpuScheduler : cpuSchedulers) {
				cpuScheduler.beginRound();
			}
			
			//execute VMs
			for (VMAllocation vmAllocation : vmList) {
				if (vmAllocation.getVm() != null && vmAllocation.getHost().getState() == Host.HostState.ON) { //ensure that a VM is instantiated within the allocation
					//update the resources required by the VMs application
					if (vmAllocation.getHost().getCpuScheduler().getState() != CpuScheduler.CpuSchedulerState.COMPLETE) {
						notDone = notDone | vmAllocation.getHost().getCpuScheduler().processVM(vmAllocation);
					}
				}
			}
			
			for (CpuScheduler cpuScheduler : cpuSchedulers) {
				cpuScheduler.endRound();
			}
			
		} while (notDone);
		
		for (CpuScheduler cpuScheduler : cpuSchedulers) {
			if (cpuScheduler.getHost().getState() == Host.HostState.ON) {
				cpuScheduler.endScheduling();
				
				cpuScheduler.getHost().getPrivDomainAllocation().getVm().completeScheduling();
			}
		}
		
		//update the resourcesInUse for each VM
		for (VMAllocation vmAllocation : vmList) {
			vmAllocation.getVm().completeScheduling();
		}

	}
}
