package edu.uwo.csd.dcsim.host.scheduler;

import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.Resources;
import edu.uwo.csd.dcsim.vm.VmAllocation;

public abstract class ResourceScheduler {

	protected Host host;
		
	/**
	 * Resets all scheduling to zero. If the host is off, scheduleResources will not be called and scheduling will remain at zero.
	 */
	public final void resetScheduling() {
		host.getPrivDomainAllocation().getVm().scheduleResources(new Resources());
		for (VmAllocation vmAlloc : host.getVMAllocations()) {
			if (vmAlloc.getVm() != null) { //null if this is an allocation for a migrating in VM
				vmAlloc.getVm().scheduleResources(new Resources());
			}
		}
	}

	public abstract void scheduleResources();
	
	public void setHost(Host host) {
		this.host = host;
	}
	
}
