package edu.uwo.csd.dcsim2.vm;

import java.util.Comparator;

public class VMAllocationTierHeightComparator implements Comparator<VMAllocation> {

	@Override
	public int compare(VMAllocation arg0, VMAllocation arg1) {
		//descending order sort
		return arg1.getVMDescription().getApplicationTier().getHeight() - arg0.getVMDescription().getApplicationTier().getHeight();
	}

}
