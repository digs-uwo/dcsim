package edu.uwo.csd.dcsim.examples.management;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.management.*;
import edu.uwo.csd.dcsim.management.action.MigrationAction;
import edu.uwo.csd.dcsim.management.capabilities.HostPoolManager;

/**
 * Implementation of IM2013 Balanced Consolidation Policy
 * 
 * @author Michael Tighe
 *
 */
public class ConsolidationPolicy extends Policy {

	double lowerThreshold;
	double upperThreshold;
	double targetUtilization;
	
	public ConsolidationPolicy(double lowerThreshold, double upperThreshold, double targetUtilization) {
		addRequiredCapability(HostPoolManager.class);
		
		this.lowerThreshold = lowerThreshold;
		this.upperThreshold = upperThreshold;
		this.targetUtilization = targetUtilization;
	}
	
	public void execute() {

		HostPoolManager hostPool = manager.getCapability(HostPoolManager.class);
		
		Collection<HostData> hosts = hostPool.getHosts();
		
		//reset the sandbox host status to the current host status
		for (HostData host : hosts) {
			host.resetSandboxStatusToCurrent();
		}
		
		ArrayList<HostData> stressed = new ArrayList<HostData>();
		ArrayList<HostData> partiallyUtilized = new ArrayList<HostData>();
		ArrayList<HostData> underUtilized = new ArrayList<HostData>();
		ArrayList<HostData> empty = new ArrayList<HostData>();
		
		classifyHosts(stressed, partiallyUtilized, underUtilized, empty, hosts);
		
		//filter out potential source hosts that have incoming migrations
		ArrayList<HostData> unsortedSources = new ArrayList<HostData>();
		for (HostData host : underUtilized) {
			if (host.getCurrentStatus().getIncomingMigrationCount() == 0) {
				unsortedSources.add(host);
			}
		}
		
		ArrayList<HostData> sources = orderSourceHosts(unsortedSources);
		ArrayList<HostData> targets = orderTargetHosts(partiallyUtilized, underUtilized);
		ArrayList<MigrationAction> migrations = new ArrayList<MigrationAction>();
		
		HashSet<HostData> usedSources = new HashSet<HostData>();
		HashSet<HostData> usedTargets = new HashSet<HostData>();
		
		for (HostData source : sources) {
			if (!usedTargets.contains(source)) { 	// Check that the source host hasn't been used as a target.
			
				ArrayList<VmStatus> vmList = this.orderSourceVms(source.getCurrentStatus().getVms());
				
				for (VmStatus vm : vmList) {
					
					for (HostData target : targets) {
						if (source != target &&
								!usedSources.contains(target) &&													//Check that the target host hasn't been used as a source.
								HostData.canHost(vm, target.getSandboxStatus(), target.getHostDescription()) &&		//target has capability and capacity to host VM
								(target.getSandboxStatus().getResourcesInUse().getCpu() + vm.getResourcesInUse().getCpu()) / 
								target.getHostDescription().getResourceCapacity().getCpu() <= targetUtilization) {				//target will not exceed target utilization
							 
							//modify host and vm states to indicate the future migration. Note we can do this because
							//we are using the designated 'sandbox' host status
							source.getSandboxStatus().migrate(vm, target.getSandboxStatus());
							
							//invalidate source and target status, as we know them to be incorrect until the next status update arrives
							source.invalidateStatus(simulation.getSimulationTime());
							target.invalidateStatus(simulation.getSimulationTime());
							
							migrations.add(new MigrationAction(source.getHostManager(),
									source.getHost(),
									target.getHost(), 
									vm.getId()));
							 
							usedTargets.add(target);
							usedSources.add(source);
							
							break;
						 }
					}
				}
			}
		}
		
		// Trigger migrations.
		for (MigrationAction migration : migrations) {
			migration.execute(simulation, this);
		}

	}
	
	private void classifyHosts(ArrayList<HostData> stressed, 
			ArrayList<HostData> partiallyUtilized, 
			ArrayList<HostData> underUtilized, 
			ArrayList<HostData> empty,
			Collection<HostData> hosts) {
		
		for (HostData host : hosts) {
			
			//filter out hosts with a currently invalid status
			if (host.isStatusValid()) {
					
				// Calculate host's avg CPU utilization in the last window of time
				double avgCpuInUse = 0;
				int count = 0;
				for (HostStatus status : host.getHistory()) {
					//only consider times when the host is powered on TODO should there be events from hosts that are off?
					if (status.getState() == Host.HostState.ON) {
						avgCpuInUse += status.getResourcesInUse().getCpu();
						++count;
					}
				}
				if (count != 0) {
					avgCpuInUse = avgCpuInUse / count;
				}
				
				double avgCpuUtilization = avgCpuInUse / host.getHostDescription().getResourceCapacity().getCpu();
				
				//classify hosts, add copies of the host so that modifications can be made
				if (host.getCurrentStatus().getVms().size() == 0) {
					empty.add(host);
				} else if (avgCpuUtilization < lowerThreshold) {
					underUtilized.add(host);
				} else if (avgCpuUtilization > upperThreshold) {
					stressed.add(host);
				} else {
					partiallyUtilized.add(host);
				}
			
			}
			
		}
		
	}
	
	
	private ArrayList<VmStatus> orderSourceVms(ArrayList<VmStatus> sourceVms) {
		
		ArrayList<VmStatus> sources = new ArrayList<VmStatus>(sourceVms);

		// Sort VMs in decreasing order by <overall capacity, CPU load>.
		// (Note: since CPU can be oversubscribed, but memory can't, memory 
		// takes priority over CPU when comparing VMs by _size_ (capacity).)
		Collections.sort(sources, VmStatusComparator.getComparator(VmStatusComparator.MEMORY, 
				VmStatusComparator.CPU_CORES, 
				VmStatusComparator.CORE_CAP, 
				VmStatusComparator.CPU_IN_USE));
		Collections.reverse(sources);
		
		return sources;
	}
	
	private ArrayList<HostData> orderSourceHosts(ArrayList<HostData> underUtilized) {

		ArrayList<HostData> sources = new ArrayList<HostData>(underUtilized);
		
		// Sort Underutilized hosts in increasing order by <power efficiency, 
		// CPU utilization>.
		Collections.sort(sources, HostDataComparator.getComparator(HostDataComparator.EFFICIENCY, HostDataComparator.CPU_UTIL));
		
		return sources;
	}
	
	private ArrayList<HostData> orderTargetHosts(ArrayList<HostData> partiallyUtilized, ArrayList<HostData> underUtilized) {
		ArrayList<HostData> targets = new ArrayList<HostData>();
		
		// Sort Partially-utilized and Underutilized hosts in decreasing order 
		// by <power efficiency, CPU utilization>.
		targets.addAll(partiallyUtilized);
		targets.addAll(underUtilized);
		Collections.sort(targets, HostDataComparator.getComparator(HostDataComparator.EFFICIENCY, HostDataComparator.CPU_UTIL));
		Collections.reverse(targets);
		
		return targets;
	}

	@Override
	public void onInstall() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onManagerStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onManagerStop() {
		// TODO Auto-generated method stub
		
	}

}
