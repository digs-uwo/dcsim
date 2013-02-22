package edu.uwo.csd.dcsim.examples.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import edu.uwo.csd.dcsim.examples.management.capabilities.HostPoolManager;
import edu.uwo.csd.dcsim.examples.management.events.ConsolidateEvent;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.management.*;
import edu.uwo.csd.dcsim.management.action.MigrationAction;

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
		super(HostPoolManager.class);
		
		this.lowerThreshold = lowerThreshold;
		this.upperThreshold = upperThreshold;
		this.targetUtilization = targetUtilization;
	}
	
	public void execute(ConsolidateEvent event) {

		HostPoolManager hostPool = manager.getCapability(HostPoolManager.class);
		
		Map<Integer, ArrayList<HostStatus>> hosts = hostPool.getHostStatus();
		
		ArrayList<HostStatus> stressed = new ArrayList<HostStatus>();
		ArrayList<HostStatus> partiallyUtilized = new ArrayList<HostStatus>();
		ArrayList<HostStatus> underUtilized = new ArrayList<HostStatus>();
		ArrayList<HostStatus> empty = new ArrayList<HostStatus>();
		
		classifyHosts(stressed, partiallyUtilized, underUtilized, empty, hosts);
		
		//filter out potential source hosts that have incoming migrations
		ArrayList<HostStatus> unsortedSources = new ArrayList<HostStatus>();
		for (HostStatus host : underUtilized) {
			if (host.getIncomingMigrationCount() == 0) {
				unsortedSources.add(host);
			}
		}
		
		ArrayList<HostStatus> sources = orderSourceHosts(unsortedSources);
		ArrayList<HostStatus> targets = orderTargetHosts(partiallyUtilized, underUtilized);
		ArrayList<MigrationAction> migrations = new ArrayList<MigrationAction>();
		
		HashSet<HostStatus> usedSources = new HashSet<HostStatus>();
		HashSet<HostStatus> usedTargets = new HashSet<HostStatus>();
		
		for (HostStatus source : sources) {
			if (!usedTargets.contains(source)) { 	// Check that the source host hasn't been used as a target.
			
				ArrayList<VmStatus> vmList = this.orderSourceVms(source.getVms());
				for (VmStatus vm : vmList) {
					for (HostStatus target : targets) {
						if (source != target &&
								!usedSources.contains(target) &&										//Check that the target host hasn't been used as a source.
								target.canHost(vm) &&														//target has capability and capacity to host VM
								(target.getResourcesInUse().getCpu() + vm.getResourcesInUse().getCpu()) / 
								target.getResourceCapacity().getCpu() <= targetUtilization) {				//target will not exceed target utilization
							 
							//modify host and vm states to indicate the future migration. Note we can do this because
							//in classifyHosts() we have made copies of all host and vm status objects
							source.migrate(vm, target);
							 
							migrations.add(new MigrationAction(hostPool.getHostManager(source.getId()),
									hostPool.getHost(source.getId()),
									hostPool.getHost(target.getId()), vm.getId()));
							 
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
	
	private void classifyHosts(ArrayList<HostStatus> stressed, 
			ArrayList<HostStatus> partiallyUtilized, 
			ArrayList<HostStatus> underUtilized, 
			ArrayList<HostStatus> empty,
			Map<Integer, ArrayList<HostStatus>> hosts) {
		
		for (int hostId : hosts.keySet()) {
			ArrayList<HostStatus> hostStatusList = hosts.get(hostId);
			HostStatus host = hostStatusList.get(0);
			
			// Calculate host's avg CPU utilization in the last window of time
			double avgCpuInUse = 0;
			int count = 0;
			for (HostStatus status : hostStatusList) {
				//only consider times when the host is powered on TODO should there be events from hosts that are off?
				if (status.getState() == Host.HostState.ON) {
					avgCpuInUse += status.getResourcesInUse().getCpu();
					++count;
				}
			}
			if (count != 0) {
				avgCpuInUse = avgCpuInUse / count;
			}
			
			double avgCpuUtilization = avgCpuInUse / host.getResourceCapacity().getCpu();
			
			//classify hosts, add copies of the host so that modifications can be made
			if (host.getVms().size() == 0) {
				empty.add(host.copy());
			} else if (avgCpuUtilization < lowerThreshold) {
				underUtilized.add(host.copy());
			} else if (avgCpuUtilization > upperThreshold) {
				stressed.add(host.copy());
			} else {
				partiallyUtilized.add(host.copy());
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
	
	private ArrayList<HostStatus> orderSourceHosts(ArrayList<HostStatus> underUtilized) {

		ArrayList<HostStatus> sources = new ArrayList<HostStatus>(underUtilized);
		
		// Sort Underutilized hosts in increasing order by <power efficiency, 
		// CPU utilization>.
		Collections.sort(sources, HostStatusComparator.getComparator(HostStatusComparator.EFFICIENCY, HostStatusComparator.CPU_UTIL));
		
		return sources;
	}
	
	private ArrayList<HostStatus> orderTargetHosts(ArrayList<HostStatus> partiallyUtilized, ArrayList<HostStatus> underUtilized) {
		ArrayList<HostStatus> targets = new ArrayList<HostStatus>();
		
		// Sort Partially-utilized and Underutilized hosts in decreasing order 
		// by <power efficiency, CPU utilization>.
		targets.addAll(partiallyUtilized);
		targets.addAll(underUtilized);
		Collections.sort(targets, HostStatusComparator.getComparator(HostStatusComparator.EFFICIENCY, HostStatusComparator.CPU_UTIL));
		Collections.reverse(targets);
		
		return targets;
	}

}
