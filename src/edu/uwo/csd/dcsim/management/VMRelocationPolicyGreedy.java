package edu.uwo.csd.dcsim.management;

import java.util.ArrayList;
import java.util.Collections;

import edu.uwo.csd.dcsim.DataCentre;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.management.action.MigrationAction;
import edu.uwo.csd.dcsim.management.stub.HostStub;
import edu.uwo.csd.dcsim.management.stub.HostStubCpuInUseComparator;
import edu.uwo.csd.dcsim.management.stub.VmStub;

public abstract class VMRelocationPolicyGreedy extends VMRelocationPolicy {

	double lowerThreshold;
	double upperThreshold;
	double targetUtilization;
	
	public VMRelocationPolicyGreedy(Simulation simulation, DataCentre dc, long interval, long firstEvent, double lowerThreshold, double upperThreshold, double targetUtilization) {
		super(simulation, dc, interval, firstEvent);
		
		this.lowerThreshold = lowerThreshold;
		this.upperThreshold = upperThreshold;
		this.targetUtilization = targetUtilization;
	}
	
	protected abstract ArrayList<HostStub> orderTargetHosts(ArrayList<HostStub> partiallyUtilized, ArrayList<HostStub> underUtilized, ArrayList<HostStub> empty);
	protected abstract ArrayList<VmStub> orderSourceVms(ArrayList<VmStub> sourceVms);
	
	@Override
	public void execute() {
		
		ArrayList<Host> hostList = dc.getHosts();
		
		//Categorize hosts
		ArrayList<HostStub> empty = new ArrayList<HostStub>();
		ArrayList<HostStub> underUtilized = new ArrayList<HostStub>();
		ArrayList<HostStub> partiallyUtilized = new ArrayList<HostStub>();
		ArrayList<HostStub> stressed = new ArrayList<HostStub>();
		
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
				
		//sort stressed list
		Collections.sort(stressed, new HostStubCpuInUseComparator());
		Collections.reverse(stressed);
		
		//create source and target lists
		ArrayList<HostStub> sources = stressed;
		ArrayList<HostStub> targets = orderTargetHosts(partiallyUtilized, underUtilized, empty);
		ArrayList<MigrationAction> migrations = new ArrayList<MigrationAction>();
		
		//iterate through source hosts
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
		
		//trigger migrations
		for (MigrationAction migration : migrations) {
			migration.execute(simulation, this);
		}
		
	}
}
