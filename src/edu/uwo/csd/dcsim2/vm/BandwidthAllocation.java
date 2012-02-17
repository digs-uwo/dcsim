package edu.uwo.csd.dcsim2.vm;

public class BandwidthAllocation {
	
	private int bandwidthAlloc;
	
	public BandwidthAllocation() {
		bandwidthAlloc = 0;
	}
	
	public BandwidthAllocation(int bandwidthAlloc) {
		this.bandwidthAlloc = bandwidthAlloc;
	}
	
	public int getBandwidthAlloc() {
		return bandwidthAlloc;
	}
	
	public void setBandwidthAlloc(int bandwidthAlloc) {
		this.bandwidthAlloc = bandwidthAlloc;
	}

}
