package edu.uwo.csd.dcsim2.management;

import java.util.Comparator;

public class HostStubVmCountComparator implements Comparator<HostStub> {

	@Override
	public int compare(HostStub arg0, HostStub arg1) {
		return arg0.getVms().size() - arg1.getVms().size();
	}

}
