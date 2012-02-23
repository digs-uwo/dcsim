package edu.uwo.csd.dcsim2.host.scheduler;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim2.vm.*;

public class RoundRobinCpuScheduler extends CpuScheduler {

	private static Logger logger = Logger.getLogger(RoundRobinCpuScheduler.class);
	
	@Override
	public void beginScheduling() {

	}

	@Override
	public void beginRound() {
		
	}

	@Override
	public boolean processVM(VMAllocation vmAllocation) {
		
		VirtualResources resourcesRequired = vmAllocation.getVm().getApplication().getResourcesRequired();
		
		double cpuScheduled = resourcesRequired.getCpu();
		
		// if vm has no demand, return false
		if (cpuScheduled == 0) {
			return false;
		}
		
		cpuScheduled = Math.min(cpuScheduled, 100);
		cpuScheduled = Math.min(cpuScheduled, availableCpu);
		
		vmAllocation.getVm().processWork(cpuScheduled);
		
		availableCpu -= cpuScheduled;
		
		if (availableCpu <= 0) {
			this.setState(CpuSchedulerState.COMPLETE);
		}

		return true;
	}

	@Override
	public void endRound() {
		
	}

	@Override
	public void endScheduling() {
		
		
	}

	@Override
	public void completeRemainingScheduling() {
		// TODO Auto-generated method stub
		
	}

}
