package edu.uwo.csd.dcsim.host.resourcemanager;

import java.util.Collection;

import edu.uwo.csd.dcsim.common.Utility;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.vm.VMAllocation;
import edu.uwo.csd.dcsim.vm.VMAllocationRequest;
import edu.uwo.csd.dcsim.vm.VMDescription;

public abstract class ResourceManager {

	protected Host host; //the host that this ResourceManager is managing
	
	/**
	 * Get the Host that this ResourceManager is managing CPU the resources of
	 * @return Host
	 */
	public final Host getHost() { 	return host; }
	
	/**
	 * Set the Host that this ResourceManager is managing the resources of
	 * @param host
	 */
	public final void setHost(Host host) { this.host = host; }
	
	/*
	 * CPU
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
	
	/**
	 * Get the total amount of CPU that has been allocated. This value may be larger than the physical CPU
	 * capacity due to oversubscription, but will always be <= the total allocation size
	 * @return
	 */
	public final int getAllocatedCpu() {
		int allocatedCpu = 0;
		
		if (host.getPrivDomainAllocation() != null) {	
			/*
			 * CPU Allocation methods
			 */
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
	public final int getAvailableCPUAllocation() { return getTotalCpu() - getAllocatedCpu(); }
	
	
	/*
	 * Memory
	 */
	
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
	
	
	/*
	 * Bandwidth
	 */
	
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
	
	
	/*
	 * Storage
	 */
	
	/**
	 * Get the total amount of storage on the Host
	 * @return
	 */
	public final long getTotalStorage() { return getHost().getStorage(); }
	
	/**
	 * Get the amount of storage that has been allocated to VMs	
	 * @return
	 */
	public final long getAllocatedStorage() {
		long storage = 0;
		
		if (host.getPrivDomainAllocation() != null)
			storage += host.getPrivDomainAllocation().getStorage();
		
		for (VMAllocation vmAllocation : host.getVMAllocations()) {
			storage += vmAllocation.getStorage();
		}
		return storage;
	}
	
	/**
	 * Get the amount of storage still available to be allocated to VMs
	 * @return
	 */
	public final long getAvailableStorage() { return getTotalStorage() - getAllocatedStorage(); }
	
	
	/*
	 * Capability and Capacity checks
	 */
	
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
		
		//check total memory
		if (vmDescription.getMemory() > getTotalMemory())
			return false;
		
		//check total bandwidth
		if (vmDescription.getBandwidth() > getTotalBandwidth())
			return false;
		
		//check total storage
		if (vmDescription.getStorage() > getTotalStorage())
			return false;
		
		return true;
	}
	
	/**
	 * Determine if the Host has enough remaining capacity to host a VM or set of VMs requiring the specified amount of resource.
	 * @return
	 */
	public abstract boolean hasCapacity(int cpu, int memory, int bandwidth, long storage);
	
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
	public abstract void allocatePrivDomain(VMAllocation privDomainAllocation, int cpu, int memory, int bandwidth, long storage);
	
}
