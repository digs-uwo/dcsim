package edu.uwo.csd.dcsim2.management;

import java.util.ArrayList;
import java.util.Collections;

import edu.uwo.csd.dcsim2.DataCentre;
import edu.uwo.csd.dcsim2.host.Host;
import edu.uwo.csd.dcsim2.management.action.MigrationAction;
import edu.uwo.csd.dcsim2.management.stub.HostStub;
import edu.uwo.csd.dcsim2.management.stub.VmStub;
import edu.uwo.csd.dcsim2.management.stub.VmStubCpuInUseComparator;
import edu.uwo.csd.dcsim2.vm.*;

public class VMAllocationPolicyMM extends VMRelocationPolicy {

	double lowerThreshold;
	double upperThreshold;
	
	public VMAllocationPolicyMM(DataCentre dc, long interval, long firstEvent, double lowerThreshold, double upperThreshold) {
		super(dc, interval, firstEvent);
		
		this.lowerThreshold = lowerThreshold;
		this.upperThreshold = upperThreshold;
		
	}

	@Override
	public void execute() {
			
		ArrayList<HostStub> hostList = HostStub.createHostStubList(dc.getHosts());
		ArrayList<VmStub> migrationList = new ArrayList<VmStub>();
		
		for (HostStub host : hostList) {
			ArrayList<VmStub> vmList = new ArrayList<VmStub>(host.getVms());
			Collections.sort(vmList, new VmStubCpuInUseComparator());
			Collections.reverse(vmList);
			
			double hUtil = host.getCpuInUse();
			double bestFitUtil = Double.MAX_VALUE;
			double upperThresholdValue  = host.getTotalCpu() * upperThreshold;
			double lowerThresholdValue = host.getTotalCpu() * lowerThreshold;
			
			while (hUtil > upperThresholdValue) {
				VmStub bestFitVm = null;
				for (VmStub vm : vmList) {
					if (vm.getCpuInUse() > hUtil - upperThresholdValue) {
						double t = vm.getCpuInUse() - hUtil + upperThresholdValue;
						if (t < bestFitUtil) {
							bestFitUtil = t;
							bestFitVm = vm;
						}
					} else {
						if (bestFitUtil == Double.MAX_VALUE) {
							bestFitVm = vm;
						}
						break;
					}
				}
				hUtil = hUtil - bestFitVm.getCpuInUse();
				migrationList.add(bestFitVm);
				vmList.remove(bestFitVm);
			}
			
			if (hUtil < lowerThresholdValue) {
				migrationList.addAll(vmList);
				vmList.clear();
			}
		}
		
		placeVMs(hostList, migrationList);
		
	}
	
	void placeVMs(ArrayList<HostStub> hostList, ArrayList<VmStub> vmList) {
				
		//place using MBFD algorithm
		
		ArrayList<MigrationAction> migrations = new ArrayList<MigrationAction>();
		
		Collections.sort(vmList, new VmStubCpuInUseComparator());
		Collections.reverse(vmList);
		
		for (VmStub vm : vmList) {
			double minPower = Double.MAX_VALUE;
			HostStub allocatedHost = null;
			for (HostStub host : hostList) {
				if (host.hasCapacity(vm) &&													//target has capacity
						 (host.getCpuInUse(vm) <= host.getTotalCpu()) &&					//target has cpu capacity
						 host.getHost().isCapable(vm.getVM().getVMDescription())) {			//target is capable
					
					double power = estimatePower(host, vm);
					if (power < minPower) {
						allocatedHost = host;
						minPower = power;
					}
				}
			}
			if (allocatedHost != null) {
				migrations.add(new MigrationAction(vm.getHost(), allocatedHost, vm));
				vm.getHost().migrate(vm, allocatedHost);
			}
		}
		
		//trigger migrations
		for (MigrationAction migration : migrations) {
			migration.execute(this);
		}
		
	}
	
	double estimatePower(HostStub host, VmStub vm) {
		double powerBefore = host.getHost().getPowerModel().getPowerConsumption(host.getCpuUtilization());
		double powerAfter = host.getHost().getPowerModel().getPowerConsumption(host.getCpuUtilization(vm));
		return powerAfter - powerBefore;
	}

	
	
}
