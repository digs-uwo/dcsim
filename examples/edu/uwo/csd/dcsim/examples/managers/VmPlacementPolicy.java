package edu.uwo.csd.dcsim.examples.managers;

import edu.uwo.csd.dcsim.management.Policy;

public class VmPlacementPolicy extends Policy {

	public VmPlacementPolicy() {
		super(HostPoolManager.class);
	}
	
}
