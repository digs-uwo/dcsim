package edu.uwo.csd.dcsim2.host.scheduler;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim2.DCSim2;
import edu.uwo.csd.dcsim2.vm.*;
import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.host.*;

public class FixedAllocCpuScheduler extends CpuScheduler {

	private static Logger logger = Logger.getLogger(FixedAllocCpuScheduler.class);
	
	private int availableCpu;
	
	@Override
	public void beginScheduling() {
		availableCpu = 0;
		for (Cpu cpu : this.getAvailableCpuCapacity()) {
			availableCpu += cpu.getCores() * cpu.getCoreCapacity();
		}
		if (availableCpu == 0) {
			this.setState(CpuSchedulerState.COMPLETE);
		}
	}

	@Override
	public void beginRound() {
		
	}

	@Override
	public boolean processVM(VMAllocation vmAllocation) {
		
		VirtualResources resourcesRequired = vmAllocation.getVm().getApplication().getResourcesRequired();
		
		int cpuScheduled = resourcesRequired.getTotalCpu();
		
		// if vm has no demand, return false
		if (cpuScheduled == 0) {
			return false;
		}
		
		cpuScheduled = Math.min(cpuScheduled, 100);
		cpuScheduled = Math.min(cpuScheduled, availableCpu);
		
		ArrayList<Integer> coresScheduled = new ArrayList<Integer>();
		for (int i = 0; i < resourcesRequired.getCores().size(); ++i) {
			coresScheduled.add((int)Math.floor(cpuScheduled / resourcesRequired.getCores().size()));
		}
		coresScheduled.set(0, coresScheduled.get(0) + cpuScheduled % resourcesRequired.getCores().size());
		
		vmAllocation.getVm().processWork(coresScheduled);
		
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
