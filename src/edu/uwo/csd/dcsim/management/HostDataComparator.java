package edu.uwo.csd.dcsim.management;

import java.util.ArrayList;
import java.util.Comparator;

import edu.uwo.csd.dcsim.host.*;
import edu.uwo.csd.dcsim.vm.VmAllocation;

/**
 * Compares host status by a (non-empty) series of attributes or factors. The 
 * available factors are:
 * 
 * + CPU_CORES:		host's total number of cores across all CPUs;
 * + CORE_CAP:		host's core capacity;
 * + MEMORY:		host's memory;
 * + BANDWIDTH:		host's bandwidth;
 * + CPU_UTIL: 		host stub's current CPU utilization;
 * + CPU_IN_USE:	host stub's current CPU in use;
 * + EFFICIENCY:	host's power efficiency at 100% CPU utilization;
 * + PWR_STATE:		host stub's current power state.
 * 
 * @author Gaston Keller
 * @author Michael Tighe modified for HostStatus
 *
 */
public enum HostDataComparator implements Comparator<HostData> {
	
	CPU_CORES {
		public int compare(HostData o1, HostData o2) {
			return o1.getHostDescription().getCpuCount() * o1.getHostDescription().getCoreCount() - o2.getHostDescription().getCpuCount() * o2.getHostDescription().getCoreCount();
		}
	},
	CORE_CAP {
		public int compare(HostData o1, HostData o2) {
			return o1.getHostDescription().getCoreCapacity() - o2.getHostDescription().getCoreCapacity();
		}
	},
	MEMORY {
		public int compare(HostData o1, HostData o2) {
			return o1.getHostDescription().getResourceCapacity().getMemory() - o2.getHostDescription().getResourceCapacity().getMemory();
		}
	},
	BANDWIDTH {
		public int compare(HostData o1, HostData o2) {
			double compare = o1.getHostDescription().getResourceCapacity().getBandwidth() - o2.getHostDescription().getResourceCapacity().getBandwidth();
			if (compare < 0)
				return -1;
			else if (compare > 0)
				return 1;
			return 0;
		}
	},
	CPU_UTIL {
		public int compare(HostData o1, HostData o2) {
			double compare = (o1.getCurrentStatus().getResourcesInUse().getCpu() / o1.getHostDescription().getResourceCapacity().getCpu()) - 
					(o2.getCurrentStatus().getResourcesInUse().getCpu() / o2.getHostDescription().getResourceCapacity().getCpu()); 
			if (compare < 0)
				return -1;
			else if (compare > 0)
				return 1;
			return 0;
		}
	},
	CPU_IN_USE {
		public int compare(HostData o1, HostData o2) {
			double compare = o1.getCurrentStatus().getResourcesInUse().getCpu() - o2.getCurrentStatus().getResourcesInUse().getCpu(); 
			if (compare < 0)
				return -1;
			else if (compare > 0)
				return 1;
			return 0;
		}
	},
	EFFICIENCY {
		public int compare(HostData o1, HostData o2) {
			double compare = o1.getHostDescription().getPowerEfficiency() - o2.getHostDescription().getPowerEfficiency();
			if (compare < 0)
				return -1;
			else if (compare > 0)
				return 1;
			return 0;
		}
	},
	PWR_STATE {
		public int compare(HostData o1, HostData o2) {
			int o1State;
			int o2State;
			
			if (o1.getCurrentStatus().getState() == Host.HostState.ON)
				o1State = 2;
			else if (o1.getCurrentStatus().getState() == Host.HostState.SUSPENDED)
				o1State = 1;
			else
				o1State = 0; //ranks off and transition states lowest
			
			if (o2.getCurrentStatus().getState() == Host.HostState.ON)
				o2State = 2;
			else if (o2.getCurrentStatus().getState() == Host.HostState.SUSPENDED)
				o2State = 1;
			else
				o2State = 0; //ranks off and transition states lowest
			
			return o1State - o2State;
		}
	},
	VOLUME_ALLOC {
		public int compare(HostData o1, HostData o2) {
			
			class Volume {
				public double calculate(HostData host) {
					ArrayList<VmAllocation> vms = new ArrayList<VmAllocation>(host.getHost().getVMAllocations());
					vms.add(host.getHost().getPrivDomainAllocation());
					
					int cpu = 0;
					int mem = 0;
					double bw = 0;
					for (VmAllocation vm : vms) {
						// If the VMAllocation has an associated VM, record its resource allocation.
						if (vm.getVm() != null)
							cpu += vm.getCpu();
							mem += vm.getMemory();
							bw += vm.getBandwidth();
					}
					
					Resources res = host.getHostDescription().getResourceCapacity();
					return (cpu * mem * bw) / (res.getCpu() * res.getMemory() * res.getBandwidth());
				}
			}
			
			double compare = new Volume().calculate(o1) - new Volume().calculate(o2);
			if (compare < 0)
				return -1;
			else if (compare > 0)
				return 1;
			return 0;
		}
	};
	
	public static Comparator<HostData> getComparator(final HostDataComparator... multipleOptions) {
        return new Comparator<HostData>() {
            public int compare(HostData o1, HostData o2) {
                for (HostDataComparator option : multipleOptions) {
                    int result = option.compare(o1, o2);
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        };
    }

}
