package edu.uwo.csd.dcsim.management.stub;

import java.util.Comparator;


public class HostStubCpuUnusedComparator implements Comparator<HostStub> {

	@Override
	public int compare(HostStub o1, HostStub o2) {
		return (int)(Math.round(o1.getUnusedCpu()) - Math.round(o2.getUnusedCpu()));
	}

}
