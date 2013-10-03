package edu.uwo.csd.dcsim.vm;

import java.util.Comparator;

public class VmAllocationRequestCpuUtilComparator implements Comparator<VmAllocationRequest> {

	@Override
	public int compare(VmAllocationRequest arg0, VmAllocationRequest arg1) {
		return arg0.getCpu() - arg1.getCpu();
	}

}
