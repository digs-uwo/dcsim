package edu.uwo.csd.dcsim2.host.scheduler;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim2.DCSim2;
import edu.uwo.csd.dcsim2.vm.*;
import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.host.*;

public class FixedAllocCpuScheduler extends CpuScheduler {

	private static Logger logger = Logger.getLogger(FixedAllocCpuScheduler.class);
	
	@Override
	public void beginScheduling() {

	}

	@Override
	public void beginRound() {
		
	}

	@Override
	public boolean processVM(VMAllocation vmAllocation) {
		
		VirtualResources resourcesRequired = vmAllocation.getVm().getApplication().getResourcesRequired();
		
		int cpuScheduled = resourcesRequired.getCpu();
		
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
