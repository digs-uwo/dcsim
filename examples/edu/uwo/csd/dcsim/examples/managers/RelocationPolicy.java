package edu.uwo.csd.dcsim.examples.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.management.HostStatus;
import edu.uwo.csd.dcsim.management.HostStatusComparator;
import edu.uwo.csd.dcsim.management.Policy;
import edu.uwo.csd.dcsim.management.VmStatus;
import edu.uwo.csd.dcsim.management.VmStatusComparator;
import edu.uwo.csd.dcsim.management.action.MigrationAction;

public class RelocationPolicy extends Policy<DataCentreAutonomicManager> {

	ArrayList<Class<? extends Event>> triggerEvents = new ArrayList<Class<? extends Event>>();
	
	double lowerThreshold;
	double upperThreshold;
	double targetUtilization;
	
	public RelocationPolicy(double lowerThreshold, double upperThreshold, double targetUtilization) {
		triggerEvents.add(RelocateEvent.class);
		
		this.lowerThreshold = lowerThreshold;
		this.upperThreshold = upperThreshold;
		this.targetUtilization = targetUtilization;
	}
	
	@Override
	public List<Class<? extends Event>> getTriggerEvents() {
		return triggerEvents;
	}

	@Override
	public boolean evaluateConditions(Event event, DataCentreAutonomicManager context,
			Simulation simulation) {

		return true;
	}
	
	@Override
	public void execute(Event event, DataCentreAutonomicManager context,
			Simulation simulation) {

		Map<Integer, ArrayList<HostStatus>> hosts = context.getHostStatus();
		
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
			ArrayList<VmStatus> vmList = orderSourceVms(source.getVmStatusList());
			
			// consider each VM within the source host
			for (VmStatus vm : vmList) {
				
				// look for a target host to receive this VM
				for (HostStatus target : targets) {
					if (target.getIncomingMigrationCount() < 2 &&										//restrict target incoming migrations to 2 for some reason
							target.canHostVm(vm) &&														//target has capability and capacity to host VM
							(target.getResourcesInUse().getCpu() + vm.getResourcesInUse().getCpu()) / 
							target.getResourceCapacity().getCpu() <= targetUtilization) {				//target will not exceed target utilization
						
						migrations.add(new MigrationAction(context.getHost(source.getId()), 
								context.getHost(target.getId()), 
								context.getHost(source.getId()).getVMAllocation(vm.getId()).getVm()));

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
			
			//classify hosts
			if (host.getVmStatusList().size() == 0) {
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
	
	public ArrayList<HostStatus> orderSourceHosts(ArrayList<HostStatus> stressed) {
		ArrayList<HostStatus> sorted = new ArrayList<HostStatus>(stressed);
		
		Collections.sort(sorted, HostStatusComparator.CPU_IN_USE);
		Collections.reverse(sorted);
		
		return sorted;
	}
	
	public ArrayList<VmStatus> orderSourceVms(ArrayList<VmStatus> sourceVms) {
		ArrayList<VmStatus> sorted = new ArrayList<VmStatus>(sourceVms);
		
		Collections.sort(sorted, VmStatusComparator.CPU_IN_USE);
		Collections.reverse(sorted);
		
		return sorted;
	}
	
	public ArrayList<HostStatus> orderTargetHosts(ArrayList<HostStatus> partiallyUtilized, ArrayList<HostStatus> underUtilized, ArrayList<HostStatus> empty) {
		ArrayList<HostStatus> sorted = new ArrayList<HostStatus>();
		
		Collections.sort(underUtilized, HostStatusComparator.CPU_IN_USE);
		Collections.reverse(underUtilized);
		
		Collections.sort(partiallyUtilized, HostStatusComparator.CPU_IN_USE);
		
		Collections.sort(empty, HostStatusComparator.PWR_STATE);
		Collections.reverse(empty);
		
		sorted.addAll(partiallyUtilized);
		sorted.addAll(underUtilized);
		sorted.addAll(empty);
		
		return sorted;
	}
	
}
