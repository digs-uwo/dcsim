package edu.uwo.csd.dcsim.management;

import java.util.Comparator;

import edu.uwo.csd.dcsim.host.*;

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
public enum HostStatusComparator implements Comparator<HostStatus> {
	
	CPU_CORES {
		public int compare(HostStatus o1, HostStatus o2) {
			return o1.getCpuCount() * o1.getCoreCount() - o2.getCpuCount() * o2.getCoreCount();
		}
	},
	CORE_CAP {
		public int compare(HostStatus o1, HostStatus o2) {
			return o1.getCoreCapacity() - o2.getCoreCapacity();
		}
	},
	MEMORY {
		public int compare(HostStatus o1, HostStatus o2) {
			return o1.getResourceCapacity().getMemory() - o2.getResourceCapacity().getMemory();
		}
	},
	BANDWIDTH {
		public int compare(HostStatus o1, HostStatus o2) {
			double compare = o1.getResourceCapacity().getBandwidth() - o2.getResourceCapacity().getBandwidth();
			if (compare < 0)
				return -1;
			else if (compare > 0)
				return 1;
			return 0;
		}
	},
	CPU_UTIL {
		public int compare(HostStatus o1, HostStatus o2) {
			double compare = (o1.getResourcesInUse().getCpu() / o1.getResourceCapacity().getCpu()) - (o2.getResourcesInUse().getCpu() / o2.getResourceCapacity().getCpu()); 
			if (compare < 0)
				return -1;
			else if (compare > 0)
				return 1;
			return 0;
		}
	},
	CPU_IN_USE {
		public int compare(HostStatus o1, HostStatus o2) {
			double compare = o1.getResourcesInUse().getCpu() - o2.getResourcesInUse().getCpu(); 
			if (compare < 0)
				return -1;
			else if (compare > 0)
				return 1;
			return 0;
		}
	},
	EFFICIENCY {
		public int compare(HostStatus o1, HostStatus o2) {
			double compare = o1.getPowerEfficiency() - o2.getPowerEfficiency();
			if (compare < 0)
				return -1;
			else if (compare > 0)
				return 1;
			return 0;
		}
	},
	PWR_STATE {
		public int compare(HostStatus o1, HostStatus o2) {
			int o1State;
			int o2State;
			
			if (o1.getState() == Host.HostState.ON)
				o1State = 2;
			else if (o1.getState() == Host.HostState.SUSPENDED)
				o1State = 1;
			else
				o1State = 0; //ranks off and transition states lowest
			
			if (o2.getState() == Host.HostState.ON)
				o2State = 2;
			else if (o2.getState() == Host.HostState.SUSPENDED)
				o2State = 1;
			else
				o2State = 0; //ranks off and transition states lowest
			
			return o1State - o2State;
		}
	};
	
	public static Comparator<HostStatus> getComparator(final HostStatusComparator... multipleOptions) {
        return new Comparator<HostStatus>() {
            public int compare(HostStatus o1, HostStatus o2) {
                for (HostStatusComparator option : multipleOptions) {
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
