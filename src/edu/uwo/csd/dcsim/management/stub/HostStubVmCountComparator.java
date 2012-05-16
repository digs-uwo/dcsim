package edu.uwo.csd.dcsim.management.stub;

import java.util.Comparator;


public class HostStubVmCountComparator implements Comparator<HostStub> {

	private Comparator<HostStub> secondary = null;
	
	public HostStubVmCountComparator() {
		
	}
	
	public HostStubVmCountComparator(Comparator<HostStub> secondary) {
		this.secondary = secondary;
	}
	
	@Override
	public int compare(HostStub arg0, HostStub arg1) {
		int val = arg0.getVms().size() - arg1.getVms().size();
		
		if (val == 0 && secondary != null)
			val = secondary.compare(arg0, arg1);
		
		return val;
	}

}
