package edu.uwo.csd.dcsim.examples.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.management.HostStatus;
import edu.uwo.csd.dcsim.management.HostStatusComparator;
import edu.uwo.csd.dcsim.management.Policy;
import edu.uwo.csd.dcsim.management.VmStatus;
import edu.uwo.csd.dcsim.management.VmStatusComparator;
import edu.uwo.csd.dcsim.management.action.MigrationAction;

/**
 * Implementation of IM2013 Balanced Relocation Policy
 * @author Michael Tighe
 *
 */
public class RelocationPolicy extends Policy {

	double lowerThreshold;
	double upperThreshold;
	double targetUtilization;
	
	public RelocationPolicy(double lowerThreshold, double upperThreshold, double targetUtilization) {
		super(HostPoolManager.class);
		
		this.lowerThreshold = lowerThreshold;
		this.upperThreshold = upperThreshold;
		this.targetUtilization = targetUtilization;
	}
	
	public void execute(RelocateEvent event) {

		HostPoolManager hostPool = manager.getCapability(HostPoolManager.class);
		
		Map<Integer, ArrayList<HostStatus>> hosts = hostPool.getHostStatus();
		
		ArrayList<HostStatus> stressed = new ArrayList<HostStatus>();
		ArrayList<HostStatus> partiallyUtilized = new ArrayList<HostStatus>();
		ArrayList<HostStatus> underUtilized = new ArrayList<HostStatus>();
		ArrayList<HostStatus> empty = new ArrayList<HostStatus>();
		
		classifyHosts(stressed, partiallyUtilized, underUtilized, empty, hosts);
				
		ArrayList<HostStatus> sources = orderSourceHosts(stressed);
		ArrayList<HostStatus> targets = orderTargetHosts(partiallyUtilized, underUtilized, empty);
		ArrayList<MigrationAction> migrations = new ArrayList<MigrationAction>();
		
		
		boolean found;
		
		// for each source host
		for (HostStatus source : sources) {

			found = false;
			ArrayList<VmStatus> vmList = orderSourceVms(source.getVms(), source);
			
			// consider each VM within the source host
			for (VmStatus vm : vmList) {
				
				// look for a target host to receive this VM
				for (HostStatus target : targets) {
					if (target.getIncomingMigrationCount() < 2 &&										//restrict target incoming migrations to 2 for some reason
							target.canHostVm(vm) &&														//target has capability and capacity to host VM
							(target.getResourcesInUse().getCpu() + vm.getResourcesInUse().getCpu()) / 
							target.getResourceCapacity().getCpu() <= targetUtilization) {				//target will not exceed target utilization
						
						//modify host and vm states to indicate the future migration. Note we can do this because
						//in classifyHosts() we have made copies of all host and vm status objects
						source.migrate(vm, target);

						migrations.add(new MigrationAction(hostPool.getHost(source.getId()), 
								hostPool.getHost(target.getId()), 
								hostPool.getHost(source.getId()).getVMAllocation(vm.getId()).getVm()));

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
	
	protected void classifyHosts(ArrayList<HostStatus> stressed, 
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
	
	public ArrayList<HostStatus> orderSourceHosts(ArrayList<HostStatus> stressed) {
		ArrayList<HostStatus> sorted = new ArrayList<HostStatus>(stressed);
		
		// Sort Stressed hosts in decreasing order by CPU utilization.
		Collections.sort(sorted, HostStatusComparator.getComparator(HostStatusComparator.CPU_UTIL));
		Collections.reverse(sorted);
		
		return sorted;
	}
	
	public ArrayList<VmStatus> orderSourceVms(ArrayList<VmStatus> sourceVms, HostStatus source) {
		
		ArrayList<VmStatus> sorted = new ArrayList<VmStatus>();
		
		// Remove VMs with less CPU load than the CPU load by which the source 
		// host is stressed.
		double cpuExcess = source.getResourcesInUse().getCpu() - source.getResourceCapacity().getCpu() * this.upperThreshold;
		for (VmStatus vm : sourceVms)
			if (vm.getResourcesInUse().getCpu() >= cpuExcess)
				sorted.add(vm);
		
		if (!sorted.isEmpty())
			// Sort VMs in increasing order by CPU load.
			Collections.sort(sorted, VmStatusComparator.getComparator(VmStatusComparator.CPU_IN_USE));
		else {
			// Add original list of VMs and sort them in decreasing order by 
			// CPU load, so as to avoid trying to migrate the smallest VMs 
			// first (which would not help resolve the stress situation).
			sorted.addAll(sourceVms);
			Collections.sort(sorted, VmStatusComparator.getComparator(VmStatusComparator.CPU_IN_USE));
			Collections.reverse(sorted);
		}
		
		return sorted;
	}
	
	public ArrayList<HostStatus> orderTargetHosts(ArrayList<HostStatus> partiallyUtilized, ArrayList<HostStatus> underUtilized, ArrayList<HostStatus> empty) {
		ArrayList<HostStatus> targets = new ArrayList<HostStatus>();
		
		// Sort Partially-utilized hosts in increasing order by 
		// <CPU utilization, power efficiency>.
		Collections.sort(partiallyUtilized, HostStatusComparator.getComparator(HostStatusComparator.CPU_UTIL, HostStatusComparator.EFFICIENCY));
		
		// Sort Underutilized hosts in decreasing order by <CPU utilization, 
		// power efficiency>.
		Collections.sort(underUtilized, HostStatusComparator.getComparator(HostStatusComparator.CPU_UTIL, HostStatusComparator.EFFICIENCY));
		Collections.reverse(underUtilized);
		
		// Sort Empty hosts in decreasing order by <power efficiency, 
		// power state>.
		Collections.sort(empty, HostStatusComparator.getComparator(HostStatusComparator.EFFICIENCY, HostStatusComparator.PWR_STATE));
		Collections.reverse(empty);
		
		targets.addAll(partiallyUtilized);
		targets.addAll(underUtilized);
		targets.addAll(empty);
		
		return targets;
	}
	
}
