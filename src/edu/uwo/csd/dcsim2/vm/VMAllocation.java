package edu.uwo.csd.dcsim2.vm;

import edu.uwo.csd.dcsim2.host.*;

public class VMAllocation {

	private VM vm;
	private VMDescription vmDescription;
	private Host host;
	private CpuAllocation cpuAllocation;
	private MemoryAllocation memoryAllocation;
	private BandwidthAllocation bandwidthAllocation;
	private StorageAllocation storageAllocation;
	
	public VMAllocation(VMDescription vmDescription, Host host) {
		this.vmDescription = vmDescription;
		this.host = host;
		vm = null;
	}
	
	public void setVm(VM vm) {
		this.vm = vm;
	}
	
	public VM getVm() {
		return vm;
	}
	
	public Host getHost() {
		return host;
	}
	
	public VMDescription getVMDescription() {
		return vmDescription;
	}
	
	public CpuAllocation getCpuAllocation() {
		return cpuAllocation;
	}
	
	public void setCpuAllocation(CpuAllocation cpuAllocation) {
		this.cpuAllocation = cpuAllocation;
	}
	
	public MemoryAllocation getMemoryAllocation() {
		return memoryAllocation;
	}
	
	public void setMemoryAllocation(MemoryAllocation memoryAllocation) {
		this.memoryAllocation = memoryAllocation;
	}
	
	public BandwidthAllocation getBandwidthAllocation() {
		return bandwidthAllocation;
	}
	
	public void setBandwidthAllocation(BandwidthAllocation bandwidthAllocation) {
		this.bandwidthAllocation = bandwidthAllocation;
	}
	
	public StorageAllocation getStorageAllocation() {
		return storageAllocation;
	}
	
	public void setStorageAllocation(StorageAllocation storageAllocation) {
		this.storageAllocation = storageAllocation;
	}
}
