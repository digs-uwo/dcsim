package edu.uwo.csd.dcsim2.host.scheduler;

import java.util.Comparator;

import edu.uwo.csd.dcsim2.vm.VMAllocation;

public class VMAllocationSchedulingComparator implements Comparator<VMAllocation> {

	@Override
	public int compare(VMAllocation arg0, VMAllocation arg1) {
		//descending order
		return arg1.getVMDescription().getApplicationTier().getHeight() - arg0.getVMDescription().getApplicationTier().getHeight();			
	}

}
