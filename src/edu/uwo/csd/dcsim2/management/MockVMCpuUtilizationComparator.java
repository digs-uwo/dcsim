package edu.uwo.csd.dcsim2.management;

import java.util.Comparator;

public class MockVMCpuUtilizationComparator implements Comparator<MockVM> {

	@Override
	public int compare(MockVM o1, MockVM o2) {
		return (int)(Math.round(o1.getCpuInUse()) -  Math.round(o2.getCpuInUse()));
	}

}
