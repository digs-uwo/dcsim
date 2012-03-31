package edu.uwo.csd.dcsim2.management;

import java.util.Comparator;

public class MockHostPowerStateComparator implements Comparator<MockHost> {

	@Override
	public int compare(MockHost o1, MockHost o2) {
		
		int val1, val2;
		if (o1.getState() == MockHost.State.OFF) {
			val1 = 0;
		} else if (o1.getState() == MockHost.State.SUSPENDED) {
			val1 = 1;
		} else{
			val1 = 2;
		}
		
		if (o2.getState() == MockHost.State.OFF) {
			val2 = 0;
		} else if (o2.getState() == MockHost.State.SUSPENDED) {
			val2 = 1;
		} else{
			val2 = 2;
		}

		return val1 - val2;
	}

	
	
}
