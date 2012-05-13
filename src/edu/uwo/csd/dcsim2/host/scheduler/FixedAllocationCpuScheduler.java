package edu.uwo.csd.dcsim2.host.scheduler;

import java.util.HashMap;

import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.core.Utility;
import edu.uwo.csd.dcsim2.vm.VMAllocation;

public class FixedAllocationCpuScheduler extends CpuScheduler {

	private HashMap<VMAllocation, Double> cpuAllocations;
	
	public FixedAllocationCpuScheduler(Simulation simulation) {
		super(simulation);
	}


	@Override
	public void beginScheduling() {
		cpuAllocations = new HashMap<VMAllocation, Double>();
		
		double cpuAllocated;
		
		//allocate privileged domain
		cpuAllocated = host.getPrivDomainAllocation().getCpu() * (simulation.getElapsedTime() / 1000d);
		cpuAllocated = Utility.roundDouble(cpuAllocated); //round off double precision problems
		cpuAllocations.put(host.getPrivDomainAllocation(), cpuAllocated);
		
		//allocate other VMs
		for (VMAllocation vmAllocation : getHost().getVMAllocations()) {
			cpuAllocated = vmAllocation.getCpu() * (simulation.getElapsedTime() / 1000d);
			cpuAllocated = Utility.roundDouble(cpuAllocated); //round off double precision problems
			cpuAllocations.put(vmAllocation, cpuAllocated);
		}
	}
	
	@Override
	public void schedulePrivDomain(VMAllocation privDomainAllocation) {
		double cpuConsumed = privDomainAllocation.getVm().execute(cpuAllocations.get(privDomainAllocation));

		consumeAvailableCpu(cpuConsumed);
	}

	@Override
	public void beginRound() {

	}

	@Override
	public boolean processVM(VMAllocation vmAllocation) {
		
		double cpuAllocated = cpuAllocations.get(vmAllocation);
		double cpuConsumed = vmAllocation.getVm().execute(cpuAllocated);
		
		if (cpuConsumed == 0)
			return false;
		
		cpuAllocations.put(vmAllocation, cpuAllocated - cpuConsumed);
		consumeAvailableCpu(cpuConsumed);

		return true;
		
	}

	@Override
	public void endRound() {

	}

	@Override
	public void endScheduling() {

	}



}
