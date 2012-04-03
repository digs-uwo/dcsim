package edu.uwo.csd.dcsim2.management;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim2.DataCentre;
import edu.uwo.csd.dcsim2.core.Event;
import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.host.*;
import edu.uwo.csd.dcsim2.host.comparator.HostCpuUtilizationComparator;
import edu.uwo.csd.dcsim2.host.comparator.HostPowerStateComparator;
import edu.uwo.csd.dcsim2.vm.*;

public class VMRelocationPolicyST03 extends VMRelocationPolicy {

	private static Logger logger = Logger.getLogger(VMRelocationPolicyST03.class);
	
	double lowerThreshold;
	double upperThreshold;
	
	public VMRelocationPolicyST03(DataCentre dc, long interval, double lowerThreshold, double upperThreshold) {
		super(dc, interval);
		
		this.lowerThreshold = lowerThreshold;
		this.upperThreshold = upperThreshold;
	}

	@Override
	public void execute() {
		ArrayList<Host> hostList = dc.getHosts();
		
		//Categorize hosts
		ArrayList<Host> empty = new ArrayList<Host>();
		ArrayList<Host> underUtilized = new ArrayList<Host>();
		ArrayList<Host> partiallyUtilized = new ArrayList<Host>();
		ArrayList<Host> stressed = new ArrayList<Host>();
		
		for (Host host : hostList) {
			double cpuUtilization = host.getCpuManager().getCpuUtilization();
			if (host.getVMAllocations().size() == 0) {
				empty.add(host);
			} else if (cpuUtilization < lowerThreshold) {
				underUtilized.add(host);
			} else if (cpuUtilization > upperThreshold) {
				stressed.add(host);
			} else {
				partiallyUtilized.add(host);
			}
		}
		
		//assemble and sort target list
		ArrayList<Host> targetList = orderTargetHosts(partiallyUtilized, underUtilized, empty);
		
		//sort stressed list
		Collections.sort(stressed, new HostCpuUtilizationComparator());
		Collections.reverse(stressed);
		
		//create mock hosts and migration list
		ArrayList<MockHost> sources = MockHost.createMockHostList(stressed);
		ArrayList<MockHost> targets = MockHost.createMockHostList(targetList);
		ArrayList<MigrationAction> migrations = new ArrayList<MigrationAction>();
		
		//iterate through source hosts
		boolean found;
		for (MockHost source : sources) {
			//sort VMs in descending order by CPU utilization
			Collections.sort(source.getVms(), new MockVMCpuUtilizationComparator());
			Collections.reverse(source.getVms());
			
			found = false;
			ArrayList<MockVM> vmList = new ArrayList<MockVM>();
			vmList.addAll(source.getVms());
			
			for (MockVM vm : vmList) {
				
				for (MockHost target : targets) {
					 if (target.getIncomingMigrationCount() < 2 &&														//target has at most 1 incoming migration pending
							 target.hasCapacity(vm) &&																	//target has capacity
							 ((target.getCpuInUse() + vm.getCpuInUse()) / target.getTotalCpu()) <= upperThreshold &&	//target will still not be stressed
							 target.getHost().isCapable(vm.getVM().getVMDescription())) {								//target is capable
						 
						 source.migrate(vm, target);
						 migrations.add(new MigrationAction(source.getHost(), target.getHost(), source, target, vm.getVM()));
						 
						 found = true;
						 break;
					 }
				}
				if (found) break;
				
			}
		}
		
		//trigger migrations
		for (MigrationAction migration : migrations) {
			VMAllocationRequest vmAllocationRequest = new VMAllocationRequest(migration.vm.getVMAllocation()); //create allocation request based on current allocation
			
			if (migration.target.getState() != Host.HostState.ON && migration.target.getState() != Host.HostState.POWERING_ON) {
				Simulation.getInstance().sendEvent(
						new Event(Host.HOST_POWER_ON_EVENT,
								Simulation.getInstance().getSimulationTime(),
								this,
								migration.target)
						);
			}
			
			migration.target.sendMigrationEvent(vmAllocationRequest, migration.vm, migration.source);
			++migrationCount;
			
			//if the source host will no longer contain any VMs, instruct it to shut down
			if (migration.mockSource.getVms().size() == 0) {
				Simulation.getInstance().sendEvent(
						new Event(Host.HOST_POWER_OFF_EVENT,
								Simulation.getInstance().getSimulationTime(),
								this,
								migration.source)
						);
			}
			
			logger.debug("Migrating VM #" + migration.vm.getId() + " from Host #" + migration.source.getId() + " to #" + migration.target.getId());
		}
		
	}
	
	protected ArrayList<Host> orderTargetHosts(ArrayList<Host> partiallyUtilized, ArrayList<Host> underUtilized, ArrayList<Host> empty) {
		ArrayList<Host> targets = new ArrayList<Host>();
		
		//sort underutilized in descending order by CPU utilization
		Collections.sort(underUtilized, new HostCpuUtilizationComparator());
		Collections.reverse(underUtilized);
		
		//sort partiallyUtilized in increasing order by CPU utilization
		Collections.sort(partiallyUtilized, new HostCpuUtilizationComparator());
		//Collections.reverse(partiallyUtilized);
		
		//sort empty by power state (on, off, suspended)
		Collections.sort(empty, new HostPowerStateComparator());
		Collections.reverse(empty);
		
		targets.addAll(partiallyUtilized);
		targets.addAll(underUtilized);
		targets.addAll(empty);
		
		return targets;
	}

	protected class MigrationAction {
		
		public Host source;
		public Host target;
		public MockHost mockSource;
		public MockHost mockTarget;
		public VM vm;
		
		public MigrationAction(Host source, Host target, MockHost mockSource, MockHost mockTarget, VM vm) {
			this.source = source;
			this.target = target;
			this.mockSource = mockSource;
			this.mockTarget = mockTarget;
			this.vm = vm;
		}
		
	}

}
