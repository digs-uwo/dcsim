package edu.uwo.csd.dcsim2.host.resourcemanager;

import edu.uwo.csd.dcsim2.vm.VMAllocationRequest;

/**
 * 
 * @author michael
 *
 */
public class StaticOversubscribingCpuManager extends StaticCpuManager {

	@Override
	public boolean hasCapacity(VMAllocationRequest vmAllocationRequest) {
		return true;
	}
	
}
