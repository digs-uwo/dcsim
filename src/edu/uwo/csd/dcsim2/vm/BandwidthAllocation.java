package edu.uwo.csd.dcsim2.vm;

public class BandwidthAllocation {
	
	private VMAllocation vmAllocation;
	private int bandwidthAlloc;
	
	public BandwidthAllocation(VMAllocation vmAllocation) {
		this.vmAllocation = vmAllocation;
	}
	
	public int getBandwidthAlloc() {
		return bandwidthAlloc;
	}
	
	public void setBandwidthAlloc(int bandwidthAlloc) {
		this.bandwidthAlloc = bandwidthAlloc;
	}
	
	public VMAllocation getVMAllocation() {
		return vmAllocation;
	}
}
