package edu.uwo.csd.dcsim2;

import java.util.Comparator;

import edu.uwo.csd.dcsim2.vm.VMAllocation;

public class VmExecutionOrderComparator implements Comparator<VMAllocation> {

	@Override
	public int compare(VMAllocation arg0, VMAllocation arg1) {
		//descending order
		return arg1.getVMDescription().getApplicationFactory().getHeight() - arg0.getVMDescription().getApplicationFactory().getHeight();			
	}

}
