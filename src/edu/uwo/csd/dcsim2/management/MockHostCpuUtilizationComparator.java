package edu.uwo.csd.dcsim2.management;

import java.util.Comparator;

public class MockHostCpuUtilizationComparator implements Comparator<MockHost> {

	@Override
	public int compare(MockHost o1, MockHost o2) {
		return (int)(Math.round(o1.getCpuInUse()) - Math.round(o2.getCpuInUse()));
	}

}
