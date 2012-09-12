package edu.uwo.csd.dcsim.management;

import java.util.ArrayList;
import java.util.Collections;

import edu.uwo.csd.dcsim.*;
import edu.uwo.csd.dcsim.common.Utility;
import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.management.action.MigrationAction;
import edu.uwo.csd.dcsim.management.stub.*;

public abstract class VMRelocationPolicyGreedy implements Daemon {

	protected DataCentre dc;
	protected DCUtilizationMonitor utilizationMonitor;
	protected double lowerThreshold;
	protected double upperThreshold;
	protected double targetUtilization;
	
	public VMRelocationPolicyGreedy(DataCentre dc, DCUtilizationMonitor utilizationMonitor, double lowerThreshold, double upperThreshold, double targetUtilization) {
		this.dc = dc;
		this.utilizationMonitor = utilizationMonitor;
		this.lowerThreshold = lowerThreshold;
		this.upperThreshold = upperThreshold;
		this.targetUtilization = targetUtilization;
	}
	
	protected abstract ArrayList<HostStub> orderTargetHosts(ArrayList<HostStub> partiallyUtilized, ArrayList<HostStub> underUtilized, ArrayList<HostStub> empty);
	protected abstract ArrayList<VmStub> orderSourceVms(ArrayList<VmStub> sourceVms);

	@Override
	public void run(Simulation simulation) {

		// Categorize hosts.
		ArrayList<HostStub> stressed = new ArrayList<HostStub>();
		ArrayList<HostStub> partiallyUtilized = new ArrayList<HostStub>();
		ArrayList<HostStub> underUtilized = new ArrayList<HostStub>();
		ArrayList<HostStub> empty = new ArrayList<HostStub>();
		
		this.classifyHosts(stressed, partiallyUtilized, underUtilized, empty);
				
		// Create source and target lists.
		ArrayList<HostStub> sources = orderSourceHosts(stressed);
		ArrayList<HostStub> targets = orderTargetHosts(partiallyUtilized, underUtilized, empty);
		ArrayList<MigrationAction> migrations = new ArrayList<MigrationAction>();
		
		// Iterate through source hosts.
		boolean found;
		for (HostStub source : sources) {
			
			found = false;
			ArrayList<VmStub> vmList = orderSourceVms(source.getVms());
			
			for (VmStub vm : vmList) {
				
				for (HostStub target : targets) {
					 if (target.getIncomingMigrationCount() < 2 &&									//target has at most 1 incoming migration pending
							 target.hasCapacity(vm) &&												//target has capacity
							 ((target.getCpuInUse(vm)) / target.getTotalCpu()) <= targetUtilization &&	//target will still not be stressed
							 target.getHost().isCapable(vm.getVM().getVMDescription())) {			//target is capable
						 
						 source.migrate(vm, target);
						 migrations.add(new MigrationAction(source, target, vm));
						 
						 found = true;
						 break;
					 }
				}
				if (found) break;
				
			}
		}
		
		// Trigger migrations.
		for (MigrationAction migration : migrations) {
			migration.execute(simulation, this);
		}
		
	}
	
	/**
	 * Classifies hosts as Stressed, Partially-Utilized, Underutilized or 
	 * Empty based on the hosts' current CPU utilization.
	 */
	protected void classifyHostsCurrentCpuUtil(ArrayList<HostStub> stressed, 
			ArrayList<HostStub> partiallyUtilized, 
			ArrayList<HostStub> underUtilized, 
			ArrayList<HostStub> empty) {
		
		ArrayList<Host> hostList = dc.getHosts();
		
		for (Host host : hostList) {
			double cpuUtilization = host.getCpuManager().getCpuUtilization();
			
			if (host.getVMAllocations().size() == 0) {
				empty.add(new HostStub(host));
			} else if (cpuUtilization < lowerThreshold) {
				underUtilized.add(new HostStub(host));
			} else if (cpuUtilization > upperThreshold) {
				stressed.add(new HostStub(host));
			} else {
				partiallyUtilized.add(new HostStub(host));
			}
		}
	}
	
	/**
	 * Classifies hosts as Stressed, Partially-Utilized, Underutilized or 
	 * Empty based on the hosts' average CPU utilization over the last CPU 
	 * load monitoring window (see DCUtilizationMonitor).
	 */
	protected void classifyHosts(ArrayList<HostStub> stressed, 
			ArrayList<HostStub> partiallyUtilized, 
			ArrayList<HostStub> underUtilized, 
			ArrayList<HostStub> empty) {
		
		ArrayList<Host> hostList = dc.getHosts();
		
		for (Host host : hostList) {
			// Calculate host's avg CPU utilization in the last window of time.
			double hostUtilValues[] = this.utilizationMonitor.getHostInUse(host).getValues();
			double avgCpuInUse = 0;
			for (Double x : hostUtilValues) {
				avgCpuInUse += x;
			}
			avgCpuInUse = avgCpuInUse / this.utilizationMonitor.getWindowSize();
			double avgCpuUtilization = Utility.roundDouble(avgCpuInUse / host.getCpuManager().getTotalCpu());
			
			if (host.getVMAllocations().size() == 0) {
				empty.add(new HostStub(host));
			} else if (avgCpuUtilization < lowerThreshold) {
				underUtilized.add(new HostStub(host));
			} else if (avgCpuUtilization > upperThreshold) {
				stressed.add(new HostStub(host));
			} else {
				partiallyUtilized.add(new HostStub(host));
			}
		}
	}
	
	/**
	 * Sorts Stressed hosts in decreasing order by CPU load (CPU in use).
	 */
	protected ArrayList<HostStub> orderSourceHosts(ArrayList<HostStub> stressed) {
		ArrayList<HostStub> sorted = new ArrayList<HostStub>(stressed);
		
		// Sort stressed hosts in decreasing order by CPU load.
		Collections.sort(sorted, HostStubComparator.getComparator(HostStubComparator.CPU_IN_USE));
		Collections.reverse(sorted);
		
		return sorted;
	}

	@Override
	public void onStart(Simulation simulation) {

	}

	@Override
	public void onStop(Simulation simulation) {
		
	}
}
