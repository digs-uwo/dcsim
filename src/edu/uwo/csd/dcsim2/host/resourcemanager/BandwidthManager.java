package edu.uwo.csd.dcsim2.host.resourcemanager;

import java.util.Collection;

import edu.uwo.csd.dcsim2.host.Host;
import edu.uwo.csd.dcsim2.vm.VMAllocation;
import edu.uwo.csd.dcsim2.vm.VMAllocationRequest;
import edu.uwo.csd.dcsim2.vm.VMDescription;

/**
 * BandwidthManager handles the allocation and deallocation of bandwidth to VMs on the Host.
 * @author Michael Tighe
 *
 */
public abstract class BandwidthManager {
	
	protected Host host; //the host that this BandwidthManager is managing resources for
	
	/**
	 * Get the Host that this BandwidthManager is managing CPU the resources of
	 * @return Host
	 */
	public final Host getHost() {	return host; }
	
	/**
	 * Set the Host that this BandwidthManager is managing the resources of
	 * @param host
	 */
	public final void setHost(Host host) { this.host = host; }
	
	/**
	 * Get the total bandwidth available on the host
	 * @return
	 */
	public final int getTotalBandwidth() { 	return getHost().getBandwidth(); }
	
	/**
	 * Get the amount of bandwidth that has been allocated to VMs
	 * @return
	 */
	public final int getAllocatedBandwidth() {
		int bandwidth = 0;
		
		if (host.getPrivDomainAllocation() != null) {
			bandwidth += host.getPrivDomainAllocation().getBandwidth();
		}
		
		for (VMAllocation allocation : host.getVMAllocations()) {
			bandwidth += allocation.getBandwidth();
		}
		return bandwidth;
	}
	
	/**
	 * Get the amount of bandwidth still available to be allocated
	 * @return
	 */
	public final int getAvailableBandwidth() { return getTotalBandwidth() - getAllocatedBandwidth(); }
	
	/**
	 * Verify whether this Host possesses the required capabilities to Host a VM with the specified
	 * VMDescription. Does not consider current allocation of other VMs running on the host.
	 * @param vmDescription
	 * @return
	 */
	public abstract boolean isCapable(VMDescription vmDescription);
	
	/**
	 * Determine if the Host has enough remaining capacity to host a VM or set of VMs requiring the specified amount of bandwidth.
	 * @param bandwidth
	 * @return
	 */
	public abstract boolean hasCapacity(int bandwidth);
	
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
