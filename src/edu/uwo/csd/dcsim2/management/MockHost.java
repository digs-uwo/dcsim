package edu.uwo.csd.dcsim2.management;

import java.util.ArrayList;

import edu.uwo.csd.dcsim2.host.*;
import edu.uwo.csd.dcsim2.vm.*;

public class MockHost {

	public enum State {ON, SUSPENDED, OFF}
	
	private Host host;
	private State state;
	private double vmmCpuInUse;
	private double vmmCpuAlloc;
	private double emptyVMAllocCpu = 0;
	private ArrayList<MockVM> vms = new ArrayList<MockVM>();
	private ArrayList<MockVM> incomingVMs = new ArrayList<MockVM>();
	private int incomingMigrationCount = 0;
	private int outgoingMigrationCount = 0;
	
	public static ArrayList<MockHost> createMockHostList(ArrayList<Host> hosts) {
		ArrayList<MockHost> mockHosts = new ArrayList<MockHost>();
		
		for (Host host : hosts)
			mockHosts.add(new MockHost(host));
		
		return mockHosts;
	}
	
	public MockHost(Host host) {
		this.host = host;
		
		if (host.getState() == Host.HostState.ON || host.getState() == Host.HostState.POWERING_ON) {
			state = State.ON;
		} else if (host.getState() == Host.HostState.SUSPENDED || host.getState() == Host.HostState.SUSPENDING) {
			state = State.SUSPENDED;
		} else {
			state = State.OFF; 
		}
		
		vmmCpuInUse = host.getPrivDomainAllocation().getVm().getResourcesInUse().getCpu();
		vmmCpuAlloc = host.getPrivDomainAllocation().getCpu();
		
		incomingMigrationCount = host.getMigratingIn().size();
		outgoingMigrationCount = host.getMigratingOut().size();
		
		for (VMAllocation vmAllocation : host.getVMAllocations()) {
			if (vmAllocation.getVm() != null) {
				vms.add(new MockVM(vmAllocation.getVm()));
			} else {
				emptyVMAllocCpu += vmAllocation.getCpu();
			}
		}

	}
	
	public Host getHost() {
		return host;
	}
	
	public State getState() {
		return state;
	}
	
	public ArrayList<MockVM> getVms() {
		return vms;
	}
	
	public double getCpuInUse() {
		double cpuInUse = vmmCpuInUse;
		for (MockVM vm : vms) {
			cpuInUse += vm.getCpuInUse();
		}
		return cpuInUse;
	}
	
	public double getCpuAllocated() {
		double cpuAlloc = vmmCpuAlloc + emptyVMAllocCpu;
		for (MockVM vm : vms) {
			cpuAlloc += vm.getCpuAlloc();
		}
		return cpuAlloc;
	}
	
	public double getTotalCpu() {
		return host.getCpuManager().getTotalCpu();
	}
	
	public double getUnusedCpu() {
		return getTotalCpu() - getCpuInUse();
	}
	
	public double getUnallocatedCpu() {
		return getTotalCpu() - getCpuAllocated();
	}
	
	public double getCpuUtilization() {
		return getCpuInUse() / getTotalCpu();
	}
	
	public int getIncomingMigrationCount() {
		return incomingMigrationCount;
	}
	
	public int getOutgoingMigrationCount() {
		return outgoingMigrationCount;
	}
	
	public void setIncomingMigrationCount(int incomingMigrationCount) {
		this.incomingMigrationCount = incomingMigrationCount;
	}
	
	public void setOutgoingMigrationCount(int outgoingMigrationCount) {
		this.outgoingMigrationCount = outgoingMigrationCount;
	}
	
	public ArrayList<MockVM> getIncomingVMs() {
		return incomingVMs;
	}
	
	public void migrate(MockVM vm, MockHost target) {
		++outgoingMigrationCount;
		
		target.setIncomingMigrationCount(target.getIncomingMigrationCount() + 1);
		vms.remove(vm);
		target.getVms().add(vm);
		target.getIncomingVMs().add(vm);
	}
	
	public boolean hasCapacity(MockVM vm) {
		//assemble a list of all VMs to be migrated
		ArrayList<VMAllocationRequest> vmAllocationRequests = new ArrayList<VMAllocationRequest>();
		
		for (MockVM incomingVM : incomingVMs) {
			vmAllocationRequests.add(new VMAllocationRequest(incomingVM.getVM().getVMAllocation()));
		}
		
		//add new VM to check
		vmAllocationRequests.add(new VMAllocationRequest(vm.getVM().getVMAllocation()));
		
		return host.hasCapacity(vmAllocationRequests);
	}
	
}
