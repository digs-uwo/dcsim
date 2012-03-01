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
		
		double cpuScheduled = Math.min(100, availableCpu);
		double cpuConsumed = vmAllocation.getVm().processWork(cpuScheduled);
		
		if (cpuConsumed == 0)
			return false;
		
		availableCpu -= cpuConsumed;
		
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
