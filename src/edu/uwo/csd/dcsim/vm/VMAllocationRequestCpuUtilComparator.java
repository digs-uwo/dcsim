package edu.uwo.csd.dcsim.vm;

import java.util.Comparator;

public class VMAllocationRequestCpuUtilComparator implements Comparator<VMAllocationRequest> {

	@Override
	public int compare(VMAllocationRequest arg0, VMAllocationRequest arg1) {
		return arg0.getCpu() - arg1.getCpu();
	}

}
