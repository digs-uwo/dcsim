package edu.uwo.csd.dcsim.host.comparator;

import java.util.Comparator;

import edu.uwo.csd.dcsim.host.*;

public class HostVMCountComparator implements Comparator<Host> {

	@Override
	public int compare(Host arg0, Host arg1) {
		double compare = arg0.getVMAllocations().size() - arg1.getVMAllocations().size(); 
		if (compare < 0)
			return -1;
		else if (compare > 0)
			return 1;
		else {
			if (arg0.getVMAllocations().size() == 0) {
				//if both hosts have no VMs, order by whether the host is on, suspended, or off
				int arg0State;
				int arg1State;
				
				if (arg0.getState() == Host.HostState.ON)
					arg0State = 3;
				else if (arg0.getState() == Host.HostState.POWERING_ON)
					arg0State = 2;
				else if (arg0.getState() == Host.HostState.SUSPENDED)
					arg0State = 1;
				else
					arg0State = 0; //ranks off and transition states lowest
				
				if (arg1.getState() == Host.HostState.ON)
					arg1State = 3;
				else if (arg1.getState() == Host.HostState.POWERING_ON)
					arg1State = 2;
				else if (arg1.getState() == Host.HostState.SUSPENDED)
					arg1State = 1;
				else
					arg1State = 0; //ranks off and transition states lowest
				return arg0State - arg1State;
			}
		}
		return 0;
	}
	
}
