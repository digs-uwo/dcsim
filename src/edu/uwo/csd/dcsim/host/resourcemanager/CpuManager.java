package edu.uwo.csd.dcsim.host.resourcemanager;

import java.util.Collection;

import edu.uwo.csd.dcsim.common.Utility;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.vm.*;

/**
 * CpuManager is responsible for allocation and deallocation of CPU resources on a Host, as well as reporting on utilization. 
 * 
 * @author Michael Tighe
 *
 */
public abstract class CpuManager {

	protected Host host; //the host that this CpuManager is managing resources for
	
	/**
	 * Get the Host that this CpuManager is managing CPU the resources of
	 * @return Host
	 */
	public final Host getHost() { 	return host; }
	
	/**
	 * Set the Host that this CpuManager is managing the resources of
	 * @param host
	 */
	public final void setHost(Host host) { this.host = host; }
	
	/*
	 * Physical CPU related methods
	 */
	
	/**
	 * Get the total physical CPU capacity of the host (total capacity of all CPUs and cores)
	 * @return
	 */
	public final int getTotalCpu() { return getHost().getTotalCpu(); }
	
	/**
	 * Get the amount of physical CPU capacity in use (real usage, not allocation)
	 * @return
	 */
	public final double getCpuInUse() {
		double cpuInUse = 0;
		
		if (host.getPrivDomainAllocation() != null) {
			cpuInUse += host.getPrivDomainAllocation().getResourcesInUse().getCpu();
		}
		
		for (VMAllocation allocation : host.getVMAllocations()) {
			cpuInUse += allocation.getResourcesInUse().getCpu();
		}
		
		return cpuInUse;
	}
	
	/**
	 * Get the fraction of physical CPU capacity that is current in use (real usage, not allocation)
	 * @return
	 */
	public final double getCpuUtilization() { return Utility.roundDouble(getCpuInUse() / getTotalCpu()); }
	
	/**
	 * Get the amount of CPU not being used (real usage, not allocation)
	 * @return
	 */
	public final double getUnusedCpu() { return getTotalCpu() - getCpuUtilization(); }
	
	/*
	 * CPU Allocation methods
	 */
	
	/**
	 * Get the total amount of CPU that has been allocated. This value may be larger than the physical CPU
	 * capacity due to oversubscription, but will always be <= the total allocation size
	 * @return
	 */
	public final int getAllocatedCpu() {
		int allocatedCpu = 0;
		
		if (host.getPrivDomainAllocation() != null) {
			allocatedCpu += host.getPrivDomainAllocation().getCpu();
		}
		
		for (VMAllocation vmAllocation : host.getVMAllocations()) {
			allocatedCpu += vmAllocation.getCpu();
		}
	
		return allocatedCpu;
	}
	
	/**
	 * Get the amount of allocation space not yet allocated
	 * @return
	 */
	public final int getAvailableAllocation() { return getTotalCpu() - getAllocatedCpu(); }
		
	/**
	 * Verify whether this Host possesses the required capabilities to Host a VM with the specified
	 * VMDescription. Does not consider current allocation of other VMs running on the host.
	 * @param vmDescription
	 * @return
	 */
	public final boolean isCapable(VMDescription vmDescription) {
		//check cores and core capacity
		if (vmDescription.getCores() > this.getHost().getCoreCount())
			return false;
		if (vmDescription.getCoreCapacity() > this.getHost().getCoreCapacity())
			return false;
				
		return true;
	}
	
	/**
	 * Determine if the Host has enough remaining capacity to host a VM or set of VMs requiring the specified amount of cpu.
	 * @param cpu
	 * @return
	 */
	public abstract boolean hasCapacity(int cpu);
	
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
