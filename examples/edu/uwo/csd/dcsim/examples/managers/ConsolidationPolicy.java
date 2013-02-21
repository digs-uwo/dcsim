package edu.uwo.csd.dcsim.examples.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.management.*;

public class ConsolidationPolicy extends Policy<DataCentreAutonomicManager> {

	ArrayList<Class<? extends Event>> triggerEvents = new ArrayList<Class<? extends Event>>();
	
	double lowerThreshold;
	double upperThreshold;
	double targetUtilization;
	
	public ConsolidationPolicy(double lowerThreshold, double upperThreshold, double targetUtilization) {
		triggerEvents.add(ConsolidateEvent.class);
		
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

//		ArrayList<HostStub> stressed = new ArrayList<HostStub>();
//		ArrayList<HostStub> partiallyUtilized = new ArrayList<HostStub>();
//		ArrayList<HostStub> underUtilized = new ArrayList<HostStub>();
//		ArrayList<HostStub> empty = new ArrayList<HostStub>();
//		
//		this.classifyHosts(stressed, partiallyUtilized, underUtilized, empty);
//		
//		// Shut down Empty hosts.
//		for (HostStub host : empty) {
//			//ensure that the host is not involved in any migrations and is not powering on
//			if (host.getIncomingMigrationCount() == 0 && host.getOutgoingMigrationCount() == 0 && host.getHost().getState() != HostState.POWERING_ON)
//				simulation.sendEvent(new Event(Host.HOST_POWER_OFF_EVENT, simulation.getSimulationTime(), this,host.getHost()));
//		}
//		
//		//filter out potential source hosts that have incoming migrations
//		ArrayList<HostStub> unsortedSources = new ArrayList<HostStub>();
//		for (HostStub host : underUtilized) {
//			if (host.getIncomingMigrationCount() == 0)
//				unsortedSources.add(host);
//		}
//		
//		// Create (sorted) source and target lists.
//		ArrayList<HostStub> sources = this.orderSourceHosts(unsortedSources);
//		ArrayList<HostStub> targets = this.orderTargetHosts(partiallyUtilized, underUtilized);
//		
//		HashSet<HostStub> usedSources = new HashSet<HostStub>();
//		HashSet<HostStub> usedTargets = new HashSet<HostStub>();
//		ArrayList<MigrationAction> migrations = new ArrayList<MigrationAction>();
//		for (HostStub source : sources) {
//			if (!usedTargets.contains(source)) { 	// Check that the source host hasn't been used as a target.
//			
//				ArrayList<VmStub> vmList = this.orderSourceVms(source.getVms());
//				for (VmStub vm : vmList) {
//					for (HostStub target : targets) {
//						 if (source != target &&
//								 !usedSources.contains(target) &&											// Check that the target host hasn't been used as a source.
//								 target.hasCapacity(vm) &&													// Target host has capacity.
//								 (target.getCpuInUse(vm) / target.getTotalCpu()) <= targetUtilization &&	// Target host will not exceed target utilization.
//								 target.getHost().isCapable(vm.getVM().getVMDescription())) {				// Target host is capable.
//							 
//							 source.migrate(vm, target);
//							 migrations.add(new MigrationAction(source, target, vm));
//							 
//							 usedTargets.add(target);
//							 usedSources.add(source);
//							 break;
//						 }
//					}
//				}
//			}
//		}
//		
//		// Trigger migrations.
//		for (MigrationAction migration : migrations) {
//			migration.execute(simulation, this);
//		}

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
