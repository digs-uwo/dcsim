package edu.uwo.csd.dcsim.management;

import java.util.ArrayList;
import java.util.Collections;

import edu.uwo.csd.dcsim.DataCentre;
import edu.uwo.csd.dcsim.core.Daemon;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.management.action.MigrationAction;
import edu.uwo.csd.dcsim.management.stub.*;

public class VMAllocationPolicyGreedy implements Daemon {

	DataCentre dc;
	double lowerThreshold;
	double targetThreshold;
	double upperThreshold;
	
	public VMAllocationPolicyGreedy(DataCentre dc,
			double lowerThreshold,
			double targetThreshold,
			double upperThreshold) {
		
		this.dc = dc;
		this.lowerThreshold = lowerThreshold;
		this.targetThreshold = targetThreshold;
		this.upperThreshold = upperThreshold;
		
	}

	@Override
	public void run(Simulation simulation) {

		ArrayList<HostStub> hostList = HostStub.createHostStubList(dc.getHosts());
		ArrayList<MigrationAction> migrationList = new ArrayList<MigrationAction>();
		
		//Categorize hosts
		ArrayList<HostStub> empty = new ArrayList<HostStub>();
		ArrayList<HostStub> underUtilized = new ArrayList<HostStub>();
		ArrayList<HostStub> partiallyUtilized = new ArrayList<HostStub>();
		ArrayList<HostStub> stressed = new ArrayList<HostStub>();
		
		for (HostStub host : hostList) {
			double cpuUtilization = host.getCpuUtilization();
			if (host.getVms().size() == 0) {
				empty.add(host);
			} else if (cpuUtilization < lowerThreshold) {
				underUtilized.add(host);
			} else if (cpuUtilization > upperThreshold) {
				stressed.add(host);
			} else {
				partiallyUtilized.add(host);
			}
		}
		
		//build relocation target list
		ArrayList<HostStub> targets = new ArrayList<HostStub>();
		Collections.sort(partiallyUtilized, new HostStubCpuUnusedComparator());
		Collections.reverse(partiallyUtilized);
		
		Collections.sort(underUtilized, new HostStubCpuUnusedComparator());
		
		
		Collections.sort(empty, new HostStubPowerStateComparator());
		Collections.reverse(empty);
		
		targets.addAll(partiallyUtilized);
		targets.addAll(underUtilized);
		targets.addAll(empty);
		
		//sort stressed list in decreasing order by CPU utilization
		ArrayList<HostStub> sources = stressed;
		Collections.sort(sources, new HostStubCpuInUseComparator());
		Collections.reverse(sources);
		
		//keep track of hosts used as migration targets to avoid using them as consolidation sources later
		ArrayList<HostStub> usedTargets = new ArrayList<HostStub>();
		
		//perform relocation from stressed hosts
		for (HostStub source : sources) {
			
			//sort VM list
			ArrayList<VmStub> vmList = new ArrayList<VmStub>(source.getVms());
			Collections.sort(vmList, new VmStubCpuInUseComparator());
			
			boolean found = false;
			for (VmStub vm : vmList) {
				
				for (HostStub target : targets) {
					if (source != target &&
							target.hasCapacity(vm) &&														//target has capacity
							 ((target.getCpuInUse(vm)) / target.getTotalCpu()) <= targetThreshold &&	//target will still not be stressed
							 target.getHost().isCapable(vm.getVM().getVMDescription())) {				//target is capable
						 
						 source.migrate(vm, target);
						 migrationList.add(new MigrationAction(source, target, vm));
						 usedTargets.add(target);
						 
						 found = true;
						 break;
					 }
				}
				
				if (found) break;
			}
		}
		
		//now perform consolidation
		sources = underUtilized;
		Collections.sort(sources, new HostStubCpuInUseComparator());
		
		targets.clear();
		targets.addAll(partiallyUtilized);
		targets.addAll(underUtilized);
		
		//keep track of sources that have already been used as a source
		ArrayList<HostStub> usedSources = new ArrayList<HostStub>();
		
		for (HostStub source : sources) {
			
			//ensure that this source host has not been used as a target for relocation
			if (!usedTargets.contains(source)) {
				 
				ArrayList<VmStub> vmList = new ArrayList<VmStub>(source.getVms()); //do not need to sort, as we are migrating all VMs
				
				for (VmStub vm : vmList) {
					
					for (HostStub target : targets) {
						//ensure this target has not already been used as a source for consolidation
						if (!usedSources.contains(target)) {
							
							if (source != target &&
									target.hasCapacity(vm) &&														//target has capacity
									 ((target.getCpuInUse(vm)) / target.getTotalCpu()) <= targetThreshold &&	//target will still not be stressed
									 target.getHost().isCapable(vm.getVM().getVMDescription())) {				//target is capable
								 
								 source.migrate(vm, target);
								 migrationList.add(new MigrationAction(source, target, vm));

								 if (!usedSources.contains(source))
									 usedSources.add(source);
								 usedTargets.add(target);
								 
								 break; //continue to next VM
							 }		
						}
					}	
				}	
			}
		}
		
		//trigger migrations
		for (MigrationAction migration : migrationList) {
			migration.execute(simulation, this);
		}
		
	}

	@Override
	public void onStart(Simulation simulation) {
		
	}

	@Override
	public void onStop(Simulation simulation) {
		
	}

}
