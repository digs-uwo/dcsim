package edu.uwo.csd.dcsim.management.stub;

import java.util.Comparator;

/**
 * Compares host stubs by a (non-empty) series of attributes or factors. The 
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
 *
 */
public enum HostStubComparator implements Comparator<HostStub> {
	
	CPU_CORES {
		public int compare(HostStub o1, HostStub o2) {
			return o1.getHost().getCpuCount() * o1.getHost().getCoreCount() - o2.getHost().getCpuCount() * o2.getHost().getCoreCount();
		}
	},
	CORE_CAP {
		public int compare(HostStub o1, HostStub o2) {
			return o1.getHost().getCoreCapacity() - o2.getHost().getCoreCapacity();
		}
	},
	MEMORY {
		public int compare(HostStub o1, HostStub o2) {
			return o1.getHost().getMemory() - o2.getHost().getMemory();
		}
	},
	BANDWIDTH {
		public int compare(HostStub o1, HostStub o2) {
			return o1.getHost().getBandwidth() - o2.getHost().getBandwidth();
		}
	},
	CPU_UTIL {
		public int compare(HostStub o1, HostStub o2) {
			double compare = o1.getCpuUtilization() - o2.getCpuUtilization(); 
			if (compare < 0)
				return -1;
			else if (compare > 0)
				return 1;
			return 0;
		}
	},
	CPU_IN_USE {
		public int compare(HostStub o1, HostStub o2) {
			double compare = o1.getCpuInUse() - o2.getCpuInUse(); 
			if (compare < 0)
				return -1;
			else if (compare > 0)
				return 1;
			return 0;
		}
	},
	EFFICIENCY {
		public int compare(HostStub o1, HostStub o2) {
			double compare = o1.getHost().getPowerEfficiency(1) - o2.getHost().getPowerEfficiency(1);
			if (compare < 0)
				return -1;
			else if (compare > 0)
				return 1;
			return 0;
		}
	},
	PWR_STATE {
		public int compare(HostStub o1, HostStub o2) {
			int o1State;
			int o2State;
			
			if (o1.getState() == HostStub.State.ON)
				o1State = 2;
			else if (o1.getState() == HostStub.State.SUSPENDED)
				o1State = 1;
			else
				o1State = 0; //ranks off and transition states lowest
			
			if (o2.getState() == HostStub.State.ON)
				o2State = 2;
			else if (o2.getState() == HostStub.State.SUSPENDED)
				o2State = 1;
			else
				o2State = 0; //ranks off and transition states lowest
			
			return o1State - o2State;
		}
	};
	
	public static Comparator<HostStub> getComparator(final HostStubComparator... multipleOptions) {
        return new Comparator<HostStub>() {
            public int compare(HostStub o1, HostStub o2) {
                for (HostStubComparator option : multipleOptions) {
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
