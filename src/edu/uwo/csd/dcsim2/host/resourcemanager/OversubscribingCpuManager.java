package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.Collection;

import edu.uwo.csd.dcsim2.vm.VMAllocationRequest;

/**
 * OversubscribingCpuManager allocates CPU to a VM provided that the Host is capable (in terms of number of cores and core capacity). It will allocate more CPU than
 * is actually available on the Host.
 * 
 * @author Michael Tighe
 *
 */
public class OversubscribingCpuManager extends SimpleCpuManager {
	
	@Override
	public boolean hasCapacity(VMAllocationRequest vmAllocationRequest) {
		
		//always return true
		return true;
	}
	
	@Override
	public boolean hasCapacity(Collection<VMAllocationRequest> vmAllocationRequests) {
		
		//always return true
		return true;
	}
	
}
