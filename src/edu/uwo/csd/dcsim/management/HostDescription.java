package edu.uwo.csd.dcsim.management;

import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.Resources;

public class HostDescription {

	private int cpus;
	private int cores;
	private int coreCapacity;
	private double powerEfficiency;
	private Resources resourceCapacity = new Resources();
	
	public HostDescription(Host host) {
		cpus = host.getCpuCount();
		cores = host.getCoreCount();
		coreCapacity = host.getCoreCapacity();
		powerEfficiency = host.getPowerEfficiency(1);
		
		resourceCapacity.setCpu(host.getTotalCpu());
		resourceCapacity.setMemory(host.getMemory());
		resourceCapacity.setBandwidth(host.getBandwidth());
		resourceCapacity.setStorage(host.getStorage());
	}
	
	public int getCpuCount() {
		return cpus;
	}
	
	public int getCoreCount() {
		return cores;
	}
	
	public int getCoreCapacity() {
		return coreCapacity;
	}
	
	public double getPowerEfficiency() {
		return powerEfficiency;
	}
	
	public Resources getResourceCapacity() {
		return resourceCapacity;
	}
	
}
