package edu.uwo.csd.dcsim2.management.stub;

import java.util.Comparator;


public class HostStubPowerStateComparator implements Comparator<HostStub> {

	@Override
	public int compare(HostStub o1, HostStub o2) {
		
		int val1, val2;
		if (o1.getState() == HostStub.State.OFF) {
			val1 = 0;
		} else if (o1.getState() == HostStub.State.SUSPENDED) {
			val1 = 1;
		} else{
			val1 = 2;
		}
		
		if (o2.getState() == HostStub.State.OFF) {
			val2 = 0;
		} else if (o2.getState() == HostStub.State.SUSPENDED) {
			val2 = 1;
		} else{
			val2 = 2;
		}

		return val1 - val2;
	}

	
	
}
