package edu.uwo.csd.dcsim2.host.scheduler;

import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.core.Utility;
import edu.uwo.csd.dcsim2.vm.VMAllocation;

public class FairShareCpuScheduler extends CpuScheduler {

	private double roundCpuShare = 0;
	private double minShare;
	private int nVms; //keeps track of the number of VMs that still have work to execute
	
	@Override
	public void beginScheduling() {
		nVms = getHost().getVMAllocations().size();
		
		if (nVms > 0) {
			minShare = 1 / nVms; //limit the smallest amount of allocation to be 1 cpu share divided by the number of VMs on the host
		}
	}
	
	@Override
	public void schedulePrivDomain(VMAllocation privDomainAllocation) {
		//schedule privileged domain as much CPU as it requires (takes priority over all other VMs)
		double cpuConsumed = privDomainAllocation.getVm().processWork(getAvailableCpu()) ;
		
		consumeAvailableCpu(cpuConsumed);
	}

	@Override
	public void beginRound() {
		roundCpuShare = getAvailableCpu() / nVms; //divide the remaining cpu by the number of VMs still executing
		roundCpuShare = Utility.roundDouble(roundCpuShare); //round off double precision problems
		if (roundCpuShare < minShare)
			roundCpuShare = minShare;
	}

	@Override
	public boolean processVM(VMAllocation vmAllocation) {
		
		double cpuAvail = Math.min(roundCpuShare, getAvailableCpu()); //overcome rounding errors that allow sightly more CPU to be used than available
		
		double cpuConsumed = vmAllocation.getVm().processWork(cpuAvail);
		
		if (cpuConsumed == 0) {
			--nVms; //once a VM does not execute any work, it will no longer have any work for this interval, due to the order in which dependent VMs are scheduled by the master scheduler
			return false;
		}
		
		consumeAvailableCpu(cpuConsumed);

		return true;
	}

	@Override
	public void endRound() {
		// TODO Auto-generated method stub
	}

	@Override
	public void endScheduling() {
		// TODO Auto-generated method stub
		
	}

	

}
