package edu.uwo.csd.dcsim2.vm;

public class VMAllocationRequest {

	private VMDescription vmDescription;
	private CpuAllocation cpuAllocation;
	private MemoryAllocation memoryAllocation;
	private BandwidthAllocation bandwidthAllocation;
	private StorageAllocation storageAllocation;

	public VMAllocationRequest(VMDescription vmDescription,
			CpuAllocation cpuAllocation, 
			MemoryAllocation memoryAllocation, 
			BandwidthAllocation bandwidthAllocation, 
			StorageAllocation storageAllocation) {
		
		this.vmDescription = vmDescription;
		this.cpuAllocation = cpuAllocation;
		this.memoryAllocation = memoryAllocation;
		this.bandwidthAllocation = bandwidthAllocation;
		this.storageAllocation = storageAllocation;
	}
	
	public VMAllocationRequest(VMAllocation vmAllocation) {
		vmDescription = vmAllocation.getVMDescription();
		
		cpuAllocation = new CpuAllocation();
		for (Integer core : vmAllocation.getCpuAllocation().getCoreAlloc()) {
			cpuAllocation.getCoreAlloc().add(core);
		}
		
		memoryAllocation = new MemoryAllocation(vmAllocation.getMemoryAllocation().getMemoryAlloc());
		bandwidthAllocation = new BandwidthAllocation(vmAllocation.getBandwidthAllocation().getBandwidthAlloc());
		storageAllocation = new StorageAllocation(vmAllocation.getStorageAllocation().getStorageAlloc());	
	}
	
	public VMAllocationRequest(VMDescription vmDescription) {
		this.vmDescription =  vmDescription;
		
		cpuAllocation = new CpuAllocation();
		for (int i = 0; i < vmDescription.getCores(); ++i) {
			cpuAllocation.getCoreAlloc().add(vmDescription.getCoreCapacity());
		}
		
		memoryAllocation = new MemoryAllocation(vmDescription.getMemory());
		bandwidthAllocation = new BandwidthAllocation(vmDescription.getBandwidth());
		storageAllocation = new StorageAllocation(vmDescription.getStorage());
	}
	
	public VMDescription getVMDescription() {
		return vmDescription;
	}

	public CpuAllocation getCpuAllocation() {
		return cpuAllocation;
	}
	
	public MemoryAllocation getMemoryAllocation() {
		return memoryAllocation;
	}
	
	public BandwidthAllocation getBandwidthAllocation() {
		return bandwidthAllocation;
	}
	
	public StorageAllocation getStorageAllocation() {
		return storageAllocation;
	}
	
}
