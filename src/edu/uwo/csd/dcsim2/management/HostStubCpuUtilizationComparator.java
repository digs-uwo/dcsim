package edu.uwo.csd.dcsim2.management;

import java.util.Comparator;

public class HostStubCpuUtilizationComparator implements Comparator<HostStub> {

	@Override
	public int compare(HostStub o1, HostStub o2) {
		return (int)(Math.round(o1.getCpuInUse()) - Math.round(o2.getCpuInUse()));
	}

}
