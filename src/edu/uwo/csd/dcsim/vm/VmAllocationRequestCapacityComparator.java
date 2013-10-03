package edu.uwo.csd.dcsim.vm;

import java.util.Comparator;

/**
 * This class compares two VM allocation requests in terms of overall resource 
 * capacity requested. Resource needs are compared in the following order: 
 * memory, CPU cores, CPU core capacity, and bandwidth.
 * 
 * @author Gaston Keller
 *
 */
public class VmAllocationRequestCapacityComparator implements Comparator<VmAllocationRequest> {

	@Override
	public int compare(VmAllocationRequest arg0, VmAllocationRequest arg1) {
		if (arg0.getMemory() != arg1.getMemory())
			return arg0.getMemory() - arg1.getMemory();
		else
			if (arg0.getVMDescription().getCores() != arg1.getVMDescription().getCores())
				return arg0.getVMDescription().getCores() - arg1.getVMDescription().getCores();
			else
				if (arg0.getVMDescription().getCoreCapacity() != arg1.getVMDescription().getCoreCapacity())
					return arg0.getVMDescription().getCoreCapacity() - arg1.getVMDescription().getCoreCapacity();
				else
					if (arg0.getBandwidth() != arg1.getBandwidth())
						return arg0.getBandwidth() - arg1.getBandwidth();
					else
						return 0;
	}

}
