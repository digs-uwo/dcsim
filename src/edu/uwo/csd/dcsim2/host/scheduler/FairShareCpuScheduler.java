package edu.uwo.csd.dcsim2.host.scheduler;

import edu.uwo.csd.dcsim2.core.Utility;
import edu.uwo.csd.dcsim2.vm.VMAllocation;

public class FairShareCpuScheduler extends CpuScheduler {

	private double roundCpuShare = 0;
	private double minShare;
	
	public static int count = 0;
	
	@Override
	public void beginScheduling() {
		if (getHost().getVMAllocations().size() > 0) {
			minShare = 1 / getHost().getVMAllocations().size(); //limit the smallest amount of allocation to be 1 cpu share divided by the number of VMs on the host
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
		//if round share drops below the minimum share, stop dividing it... otherwise, it may divide infinitely and never complete scheduling
		if (roundCpuShare >= minShare || roundCpuShare == 0) {
			roundCpuShare = getAvailableCpu() / getHost().getVMAllocations().size();
			roundCpuShare = Utility.roundDouble(roundCpuShare); //round off double precision problems
		}
	}

	@Override
	public boolean processVM(VMAllocation vmAllocation) {
		++count;
		double cpuConsumed = vmAllocation.getVm().processWork(roundCpuShare);
		
		if (cpuConsumed == 0)
			return false;
		
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
