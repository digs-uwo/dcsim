package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.Collection;

import edu.uwo.csd.dcsim2.host.Host;
import edu.uwo.csd.dcsim2.vm.VMAllocation;
import edu.uwo.csd.dcsim2.vm.VMAllocationRequest;
import edu.uwo.csd.dcsim2.vm.VMDescription;

/**
 * MemoryManager handles the allocation and deallocation of memory to VMs on the Host.
 * @author Michael Tighe
 *
 */
public abstract class MemoryManager {
	
	protected Host host; //the host that this ResourceManager is managing resources for
	
	/**
	 * Get the Host that this ResourceManager is managing memory the resources of
	 * @return Host
	 */
	public final Host getHost() { return host; }
	
	/**
	 * Set the Host that this ResourceManager is managing the resources of
	 * @param host
	 */
	public final void setHost(Host host) { this.host = host; }
	
	/**
	 * Get the amount of memory that has been allocated
	 * @return
	 */
	public final int getAllocatedMemory() {
		int memory = 0;
		
		if (host.getPrivDomainAllocation() != null) {
			memory += host.getPrivDomainAllocation().getMemory();
		}
		
		for (VMAllocation vmAllocation : host.getVMAllocations()) {
			memory += vmAllocation.getMemory();
		}
		return memory;
	}
	
	/**
	 * Get the amount of memory still available to be allocated
	 * @return
	 */
	public final int getAvailableMemory() { return getTotalMemory() - getAllocatedMemory(); }
	
	/**
	 * Get the total amount of memory on the Host
	 * @return
	 */
	public final int getTotalMemory() { return getHost().getMemory(); }
	
	/**
	 * Verify whether this Host possesses the required capabilities to Host a VM with the specified
	 * VMDescription. Does not consider current allocation of other VMs running on the host.
	 * @param vmDescription
	 * @return
	 */
	public abstract boolean isCapable(VMDescription vmDescription);
	
	/**
	 * Determine if the Host has enough remaining capacity to host a VM or set of VMs requiring the specified amount of memory.
	 * @param memory
	 * @return
	 */
	public abstract boolean hasCapacity(int memory);
	
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
	public abstract boolean hasCapacity(Collection<VMAllocationRequest> vmAllocationRequests);
	
	/**
	 * Allocate resources to a VMAllocation based on requested resources in the VMAllocationRequest
	 * @param vmAllocationRequest Requested resource allocation
	 * @param vmAllocation Actual allocation object to grant request resources to
	 * @return
	 */
	public abstract boolean allocateResource(VMAllocationRequest vmAllocationRequest, VMAllocation vmAllocation);
	
	/**
	 * Deallocate resources from the VMAllocation
	 * @param vmAllocation
	 */
	public abstract void deallocateResource(VMAllocation vmAllocation);
	
	/**
	 * Allocate resources to the privileged domain
	 * @param privDomainAllocation
	 */
	public abstract void allocatePrivDomain(VMAllocation privDomainAllocation, int allocation);
}
