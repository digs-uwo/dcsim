package edu.uwo.csd.dcsim.management;

import java.util.ArrayList;
import java.util.Collections;

import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.host.*;
import edu.uwo.csd.dcsim.vm.*;

/**
 * A VMPlacementPolicy implementing the 'Modified Best Fit Decreasing' algorithm found in "Adaptive Threshold-Based Approach for
 * Energy-Efficient Consolidation of Virtual Machines in Cloud Data Centers", Anton Beloglazovv and Rajkumar Buyya, MGC 2010.
 * 
 * @author Michael Tighe
 *
 */
public class VMPlacementPolicyMBFD extends VMPlacementPolicy {

	public VMPlacementPolicyMBFD(Simulation simulation) {
		super(simulation);
	}

	@Override
	public boolean submitVM(VMAllocationRequest vmAllocationRequest) {
		ArrayList<VMAllocationRequest> vmAllocationRequests = new ArrayList<VMAllocationRequest>();
		vmAllocationRequests.add(vmAllocationRequest);
		
		return submitVMs(vmAllocationRequests);
	}

	@Override
	public boolean submitVMs(ArrayList<VMAllocationRequest> vmAllocationRequests) {
		
		boolean success = true;
		
		//sort vm list by decreasing utilization
		Collections.sort(vmAllocationRequests, new VMAllocationRequestCpuUtilComparator());
		Collections.reverse(vmAllocationRequests);
		
		//for each vm in list
		for (VMAllocationRequest vmAllocationRequest : vmAllocationRequests) {
			//minPower = MAX
			double minPower = Double.MAX_VALUE;
			//allocatedHost = NULL
			Host allocatedHost = null;
			
			//for each host in host list
			for (Host host : datacentre.getHosts()) {
				//if host has enough resource for VM
				if (host.isCapable(vmAllocationRequest.getVMDescription()) &&
						host.getResourceManager().hasCapacity(vmAllocationRequest)
						&& host.getResourceManager().getAvailableCPUAllocation() >= vmAllocationRequest.getCpu()) {
					
					//power = estimate power
					double power = estimatePower(host, vmAllocationRequest);
					
					//if power < minPower then
					if (power < minPower) {
						//allocatedHost = host
						allocatedHost = host;
						//minPower = power
						minPower = power;
					}
				}
			}
			//if allocatedHost != null
			if (allocatedHost != null) {
				//allocate vm to allocated host
				success = success & submitVM(vmAllocationRequest, allocatedHost);
			}
			
		}
		
		return success;
	}
	
	private double estimatePower(Host host, VMAllocationRequest vmAllocationRequest) {
		double cpu = 0;
		for (VMAllocation vmAllocation : host.getVMAllocations()) {
			cpu += vmAllocation.getCpu();
		}
		
		double powerBefore = host.getPowerModel().getPowerConsumption(cpu);
		cpu += vmAllocationRequest.getCpu();
		double powerAfter = host.getPowerModel().getPowerConsumption(cpu);
		
		return powerAfter - powerBefore;		
	}

}
