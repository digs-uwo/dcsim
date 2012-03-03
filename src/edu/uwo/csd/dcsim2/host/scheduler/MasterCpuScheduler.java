package edu.uwo.csd.dcsim2.host.scheduler;

import java.util.*;

import edu.uwo.csd.dcsim2.vm.*;
import edu.uwo.csd.dcsim2.host.Host;

public class MasterCpuScheduler {

	private static MasterCpuScheduler masterCpuScheduler = new MasterCpuScheduler();
	
	private ArrayList<CpuScheduler> cpuSchedulers;
	private long maxSchedulingCount = 0;
	
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
			cpuScheduler.prepareScheduler();
			cpuScheduler.beginScheduling();
		}
		
		boolean notDone = true;
		do {
			notDone = false;
			
			for (CpuScheduler cpuScheduler : cpuSchedulers) {
				cpuScheduler.beginRound();
			}
			
			//execute VMs
			for (VMAllocation vmAllocation : vmList) {
				if (vmAllocation.getVm() != null) { //ensure that a VM is instantiated within the allocation
					//update the resources required by the VMs application
					if (vmAllocation.getHost().getCpuScheduler().getState() != CpuScheduler.CpuSchedulerState.COMPLETE) {
						vmAllocation.getVm().getApplication().updateResourcesRequired();
						notDone = notDone | vmAllocation.getHost().getCpuScheduler().processVM(vmAllocation);
						
						//advance vmAllocation scheduling count
						if (vmAllocation.getSchedulingCount() + 1 > maxSchedulingCount) {
							maxSchedulingCount = vmAllocation.getSchedulingCount() + 1;
						}
						vmAllocation.setSchedulingCount(maxSchedulingCount);
					}
				}
			}
			
			for (CpuScheduler cpuScheduler : cpuSchedulers) {
				cpuScheduler.endRound();
			}
			
		} while (notDone);
		
		//update the resourcesInUse for each VM
		for (VMAllocation vmAllocation : vmList) {
			vmAllocation.getVm().completeScheduling();
		}
		
		for (CpuScheduler cpuScheduler : cpuSchedulers) {
			cpuScheduler.endScheduling();
		}

	}
}
