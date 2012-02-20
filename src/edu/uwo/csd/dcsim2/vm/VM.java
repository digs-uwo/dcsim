package edu.uwo.csd.dcsim2.vm;

import java.util.ArrayList;

import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.application.*;

public class VM extends SimulationEntity {

	private static int nextId = 1;
	
	private int id;
	private VMDescription vmDescription;
	private ArrayList<Integer> vCoreInUse;
	private int memoryInUse;
	private int bandwidthInUse;
	private long storageInUse;
	
	private Application application;
	
	private VMAllocation vmAllocation;
	
	public VM(VMDescription vmDescription, Application application) {
		this.id = nextId++;
		this.vmDescription = vmDescription;
		this.application = application;
	
		//initialize core list
		vCoreInUse = new ArrayList<Integer>();
		for (int i = 0; i < vmDescription.getVCores(); ++i) {
			vCoreInUse.add(0);
		}
		
		memoryInUse = 0;
		bandwidthInUse = 0;
		storageInUse = 0;
		
		vmAllocation = null;
	}
	
	public int getId() {
		return id;
	}
	
	public Application getApplication() {
		return application;
	}
	
	public VMDescription getVMDescription() {
		return vmDescription;
	}
	
	public VMAllocation getVMAllocation() {
		return vmAllocation;
	}
	
	public void setVMAllocation(VMAllocation vmAllocation) {
		this.vmAllocation = vmAllocation;
	}
	
	public ArrayList<Integer> getVCoreInUse() {
		return vCoreInUse;
	}
	
	public int getMemoryInUse() {
		return memoryInUse;
	}
	
	public void setMemoryInUse(int memoryInUse) {
		this.memoryInUse = memoryInUse;
	}
	
	public int getBandwidthInUse() {
		return bandwidthInUse;
	}
	
	public void setBandwidthInUse(int bandwidthInUse) {
		this.bandwidthInUse = bandwidthInUse;
	}
	
	public long getStorageInUse() {
		return storageInUse;
	}
	
	public void setStorageInUse(long storageInUse) {
		this.storageInUse = storageInUse;
	}
	
	@Override
	public void handleEvent(Event e) {
		// TODO Auto-generated method stub
		
	}


}
