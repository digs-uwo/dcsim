package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.ArrayList;

import edu.uwo.csd.dcsim2.host.Host;
import edu.uwo.csd.dcsim2.vm.*;

public abstract class ResourceManager {

	private Host host;
	
	public Host getHost() {
		return host;
	}
	
	public void setHost(Host host) {
		this.host = host;
	}
	
	/**
	 * Verify whether this Host possesses the required capabilities to Host a VM with the specified
	 * VMDescription. Does not consider current allocation of other VMs running on the host.
	 * @param vmDescription
	 * @return
	 */
	public abstract boolean isCapable(VMDescription vmDescription);
	
	/**
	 * Determine if the Host has enough remaining capacity to host the VM.
	 * @param vmAllocate
	 * @return
	 */
	public abstract boolean hasCapacity(VMAllocationRequest vmAllocationRequest);
	
	/**
	 * Determine if the Host has enough remaining capacity to host a set of VMs
	 * @param vmAllocationRequests
	 * @return
	 */
	public abstract boolean hasCapacity(ArrayList<VMAllocationRequest> vmAllocationRequests);
	
	public abstract boolean allocateResource(VMAllocationRequest vmAllocationRequest, VMAllocation vmAllocation);
	public abstract void deallocateResource(VMAllocation vmAllocation);
	public abstract void updateAllocations();
	public abstract void allocatePrivDomain(VMAllocation privDomainAllocation);
}
