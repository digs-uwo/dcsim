package edu.uwo.csd.dcsim;

import java.util.Comparator;

import edu.uwo.csd.dcsim.vm.VMAllocation;

/**
 * Orders VMs by application height. Application height indicates how many other levels of VMs (application tiers) a VM depends on.
 * 
 * @author Michael Tighe
 *
 */
public class VmExecutionOrderComparator implements Comparator<VMAllocation> {

	@Override
	public int compare(VMAllocation arg0, VMAllocation arg1) {
		//ascending order
		return arg0.getVMDescription().getApplicationFactory().getDepth() - arg1.getVMDescription().getApplicationFactory().getDepth();			
	}

}
