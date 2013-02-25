package edu.uwo.csd.dcsim.management;

import java.util.ArrayList;

import edu.uwo.csd.dcsim.host.*;
import edu.uwo.csd.dcsim.vm.*;

public class HostStatus {
	
	private long timeStamp;
	private int id;
	private int incomingMigrations;
	private int outgoingMigrations;
	private ArrayList<VmStatus> migratingInVms = new ArrayList<VmStatus>();
	private Host.HostState state;

	private double powerConsumption;
	
	VmStatus privDomain;
	ArrayList<VmStatus> vms = new ArrayList<VmStatus>();
		
	public HostStatus(Host host, long timeStamp) {
		
		this.timeStamp = timeStamp;
		
		id = host.getId();
		incomingMigrations = host.getMigratingIn().size();
		outgoingMigrations = host.getMigratingOut().size();
		state = host.getState();

		powerConsumption = host.getCurrentPowerConsumption();
		
		privDomain = new VmStatus(host.getPrivDomainAllocation().getVm(), timeStamp);
		
		for (VMAllocation vmAlloc : host.getVMAllocations()) {
			if (vmAlloc.getVm() != null) {
				vms.add(new VmStatus(vmAlloc.getVm(), timeStamp));
			}
		}
		
		//keep track of resources promised to incoming VMs
		for (VMAllocation vmAlloc : host.getMigratingIn()) {
			Resources resources = new Resources();
			resources.setCpu(vmAlloc.getVMDescription().getCpu());
			resources.setMemory(vmAlloc.getVMDescription().getMemory());
			resources.setBandwidth(vmAlloc.getVMDescription().getBandwidth());
			resources.setStorage(vmAlloc.getVMDescription().getStorage());
			
			migratingInVms.add(new VmStatus(vmAlloc.getVMDescription().getCores(),
					vmAlloc.getVMDescription().getCoreCapacity(),
					resources));
		}
		
	}
	
	public HostStatus(HostStatus host) {
		timeStamp = host.timeStamp;
		
		id = host.id;
		incomingMigrations = host.incomingMigrations;
		outgoingMigrations = host.outgoingMigrations;
		state = host.state;
		
		powerConsumption = host.powerConsumption;
		
		privDomain = host.privDomain.copy();
		
		for (VmStatus vm : host.vms) {
			vms.add(vm.copy());
		}
	}
	
	public void instantiateVm(VmStatus vm) {
		vms.add(vm);
	}
	
	public void migrate(VmStatus vm, HostStatus target) {
		++outgoingMigrations;
		
		vms.remove(vm);
		target.vms.add(vm);
		++target.incomingMigrations;
	}
	
	public long getTimeStamp() {
		return timeStamp;
	}
	
	public int getId() {
		return id;
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
	
	public ArrayList<VmStatus> getVms() {
		return vms;
	}
	
	public Resources getResourcesInUse() {
		Resources resourcesInUse = privDomain.getResourcesInUse();
		
		for (VmStatus vmStatus : vms) {
			resourcesInUse = resourcesInUse.add(vmStatus.getResourcesInUse());
		}
		
		//add resources promised to incoming VMs
		for (VmStatus vmStatus : migratingInVms) {
			resourcesInUse = resourcesInUse.add(vmStatus.getResourcesInUse());
		}
		
		return resourcesInUse;
	}
	
	public double getPowerConsumption() {
		return powerConsumption;
	}

	public HostStatus copy() {
		return new HostStatus(this);
	}
	
}
