package edu.uwo.csd.dcsim;

import java.util.*;

import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.scheduler.CpuScheduler;
import edu.uwo.csd.dcsim.vm.*;

/**
 * VmExcecutionDirector handles executing VMs in the simulation. Due to dependencies between VMs which may reside on any Host, 
 * this must be done carefully and in a specific order.
 * 
 * @author Michael Tighe
 *
 */
public final class VmExecutionDirector {
	
	/**
	 * Construct a list of VMs that must be executed
	 * @param hosts
	 * @return
	 */
	private ArrayList<VMAllocation> buildVmList(ArrayList<Host> hosts) {
		ArrayList<VMAllocation> vmList = new ArrayList<VMAllocation>();
		
		for (Host host : hosts) {
			vmList.addAll(host.getVMAllocations());
		}
		Collections.sort(vmList, new VmExecutionOrderComparator());
		
		return vmList;
	}
	
	/**
	 * Execute the VMs in the simulation
	 * @param hosts
	 */
	public void execute(ArrayList<Host> hosts) {

		//retrieve ordered list of vm allocations
		ArrayList<VMAllocation> vmList = buildVmList(hosts);
		
		//calculate the resources each VM has available
		for (VMAllocation vmAllocation : vmList) {
			if (vmAllocation.getVm() != null)
				vmAllocation.getVm().prepareExecution();
		}
		
		//prepare hosts for VM execution
		for (Host host : hosts) {
			//if the Host is ON
			if (host.getState() == Host.HostState.ON) {
				//allow the Host's CPU Scheduler to prepare for scheduling
				host.getCpuScheduler().prepareScheduler();
				host.getCpuScheduler().beginScheduling();
				
				//prepare the privileged domain VM for execution
				host.getPrivDomainAllocation().getVm().prepareExecution();
				
				//instruct the Host's CPU Scheduler to run the priviledged domain VM
				host.getCpuScheduler().schedulePrivDomain(host.getPrivDomainAllocation());
			}
		}
		
		HashSet<VMAllocation> completedVms = new HashSet<VMAllocation>(); //set of VMs that have completed execution
		boolean notDone = true; //true while execution is not complete
		
		//execute VMs in rounds until complete. In each round, every VM has a chance to execute
		do {
			notDone = false; //start by assuming done
			
			//instruct Host CPU Schedulers to begin a round of scheduling
			for (Host host : hosts) {
				if (host.getState() == Host.HostState.ON)
					host.getCpuScheduler().beginRound();
			}
			
			//execute VMs
			for (VMAllocation vmAllocation : vmList) {
				//if the VM has not be executed, the VM Allocation actually contains a VM, and the host is ON
				if (!completedVms.contains(vmAllocation) && vmAllocation.getVm() != null && vmAllocation.getHost().getState() == Host.HostState.ON) { //ensure that a VM is instantiated within the allocation					
					//if the CPU scheduler has not indicated that is is COMPLETE (i.e. out of resources)
					if (vmAllocation.getHost().getCpuScheduler().getState() != CpuScheduler.CpuSchedulerState.COMPLETE) {
						//run the VM
						if (vmAllocation.getHost().getCpuScheduler().processVM(vmAllocation)) {
							//returned true = VM is not finished executing (still has work to complete)
							notDone = true; //not done yet
						} else {
							//returned false = VM is done (no more work to complete)
							completedVms.add(vmAllocation);
						}
					}
				}
			}
			
			//instruct Host CPU Schedulers that the round has completed
			for (Host host : hosts) {
				if (host.getState() == Host.HostState.ON)
					host.getCpuScheduler().endRound();
			}
			
		} while (notDone); //if not done, execute another round
		
		
		//instruct Host CPU Schedulers that scheduling is complete
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
