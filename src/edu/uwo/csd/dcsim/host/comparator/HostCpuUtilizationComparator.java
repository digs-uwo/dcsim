package edu.uwo.csd.dcsim.host.comparator;

import java.util.Comparator;

import edu.uwo.csd.dcsim.host.Host;

public class HostCpuUtilizationComparator implements Comparator<Host> {

	@Override
	public int compare(Host arg0, Host arg1) {
		double compare = arg0.getResourceManager().getCpuUtilization() - arg1.getResourceManager().getCpuUtilization(); 
		if (compare < 0)
			return -1;
		else if (compare > 0)
			return 1;
		return 0;
	}
	
}
