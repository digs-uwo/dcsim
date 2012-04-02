package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.ArrayList;

import edu.uwo.csd.dcsim2.vm.VMAllocationRequest;

/**
 * 
 * @author michael
 *
 */
public class StaticOversubscribingCpuManager extends StaticCpuManager {

	public StaticOversubscribingCpuManager(int privDomainAlloc) {
		super(privDomainAlloc);
	}
	
	@Override
	public boolean hasCapacity(VMAllocationRequest vmAllocationRequest) {
		return true;
	}
	
	@Override
	public boolean hasCapacity(
			ArrayList<VMAllocationRequest> vmAllocationRequests) {
		return true;
	}
	
}
