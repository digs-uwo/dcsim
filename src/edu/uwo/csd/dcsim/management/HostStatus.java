package edu.uwo.csd.dcsim.management;

import java.util.ArrayList;

import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.host.*;
import edu.uwo.csd.dcsim.vm.*;

public class HostStatus {
	
	private long timeStamp;
	private int id;
	private int cpus;
	private int cores;
	private int coreCapacity;
	private int incomingMigrations;
	private int outgoingMigrations;
	private double powerEfficiency;
	Host.HostState state;
	private Resources resourceCapacity = new Resources();

	private double powerConsumption;
	
	VmStatus privDomain;
	ArrayList<VmStatus> vms = new ArrayList<VmStatus>();
	
	public HostStatus(Host host, Simulation simulation) {
		
		timeStamp = simulation.getSimulationTime();
		
		id = host.getId();
		cpus = host.getCpuCount();
		cores = host.getCoreCount();
		coreCapacity = host.getCoreCapacity();
		incomingMigrations = host.getMigratingIn().size();
		outgoingMigrations = host.getMigratingOut().size();
		powerEfficiency = host.getPowerEfficiency(1);
		state = host.getState();
		
		resourceCapacity.setCpu(host.getTotalCpu());
		resourceCapacity.setMemory(host.getMemory());
		resourceCapacity.setBandwidth(host.getBandwidth());
		resourceCapacity.setStorage(host.getStorage());
		powerConsumption = host.getCurrentPowerConsumption();
		
		privDomain = new VmStatus(host.getPrivDomainAllocation().getVm(), simulation);
		
		for (VMAllocation vmAlloc : host.getVMAllocations()) {
			vms.add(new VmStatus(vmAlloc.getVm(), simulation));
		}
		
	}
	
	public long getTimeStamp() {
		return timeStamp;
	}
	
	public int getId() {
		return id;
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
	
	public Host.HostState getState() {
		return state;
	}
	
	public VmStatus getPrivDomainState() {
		return privDomain;
	}
	
	public int getIncomingMigrationCount() {
		return incomingMigrations;
	}
	
	public int getOutgoingMigrationCount() {
		return outgoingMigrations;
	}
	
	public ArrayList<VmStatus> getVmStatusList() {
		return vms;
	}
	
	public Resources getResourcesInUse() {
		Resources resourcesInUse = privDomain.getResourcesInUse();
		
		for (VmStatus vmStatus : vms) {
			resourcesInUse = resourcesInUse.add(vmStatus.getResourcesInUse());
		}
		
		return resourcesInUse;
	}
	
	public Resources getResourceCapacity() {
		return resourceCapacity;
	}
	
	public double getPowerConsumption() {
		return powerConsumption;
	}
	
	public boolean canHostVm(VmStatus vm) {
		//verify that this host can host the given vm
		
		//check capabilities (e.g. core count, core capacity)
		if (cpus * cores < vm.cores)
			return false;
		if (coreCapacity < vm.getCoreCapacity())
			return false;
		
		//check available resource
		Resources resourcesInUse = getResourcesInUse();
		if (resourceCapacity.getCpu() - resourcesInUse.getCpu() < vm.getResourcesInUse().getCpu())
			return false;
		if (resourceCapacity.getMemory() - resourcesInUse.getMemory() < vm.getResourcesInUse().getMemory())
			return false;
		if (resourceCapacity.getBandwidth() - resourcesInUse.getBandwidth() < vm.getResourcesInUse().getBandwidth())
			return false;
		if (resourceCapacity.getStorage() - resourcesInUse.getStorage() < vm.getResourcesInUse().getStorage())
			return false;
		
		
		return true;
	}
	
}
