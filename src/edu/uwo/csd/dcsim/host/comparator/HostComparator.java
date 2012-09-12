package edu.uwo.csd.dcsim.host.comparator;

import java.util.Comparator;
import edu.uwo.csd.dcsim.host.Host;

/**
 * Compares hosts by a (non-empty) series of attributes or factors. The 
 * available factors are:
 * 
 * + CPU_CORES:		total number of cores across all CPUs;
 * + CORE_CAP:		core capacity;
 * + MEMORY:		memory;
 * + BANDWIDTH:		bandwidth;
 * + CPU_UTIL: 		current CPU utilization;
 * + CPU_IN_USE:	current CPU in use;
 * + EFFICIENCY:	power efficiency of the host at 100% CPU utilization;
 * + PWR_STATE:		current power state.
 * 
 * @author Gaston Keller
 *
 */
public enum HostComparator implements Comparator<Host> {
	
	CPU_CORES {
		public int compare(Host o1, Host o2) {
			return o1.getCpuCount() * o1.getCoreCount() - o2.getCpuCount() * o2.getCoreCount();
		}
	},
	CORE_CAP {
		public int compare(Host o1, Host o2) {
			return o1.getCoreCapacity() - o2.getCoreCapacity();
		}
	},
	MEMORY {
		public int compare(Host o1, Host o2) {
			return o1.getMemory() - o2.getMemory();
		}
	},
	BANDWIDTH {
		public int compare(Host o1, Host o2) {
			return o1.getBandwidth() - o2.getBandwidth();
		}
	},
	CPU_UTIL {
		public int compare(Host o1, Host o2) {
			double compare = o1.getCpuManager().getCpuUtilization() - o2.getCpuManager().getCpuUtilization(); 
			if (compare < 0)
				return -1;
			else if (compare > 0)
				return 1;
			return 0;
		}
	},
	CPU_IN_USE {
		public int compare(Host o1, Host o2) {
			double compare = o1.getCpuManager().getCpuInUse() - o2.getCpuManager().getCpuInUse(); 
			if (compare < 0)
				return -1;
			else if (compare > 0)
				return 1;
			return 0;
		}
	},
	EFFICIENCY {
		public int compare(Host o1, Host o2) {
			double compare = o1.getPowerEfficiency(1) - o2.getPowerEfficiency(1);
			if (compare < 0)
				return -1;
			else if (compare > 0)
				return 1;
			return 0;
		}
	},
	PWR_STATE {
		public int compare(Host o1, Host o2) {
			int o1State;
			int o2State;
			
			if (o1.getState() == Host.HostState.ON)
				o1State = 3;
			else if (o1.getState() == Host.HostState.POWERING_ON)
				o1State = 2;
			else if (o1.getState() == Host.HostState.SUSPENDED)
				o1State = 1;
			else
				o1State = 0; //ranks off and transition states lowest
			
			if (o2.getState() == Host.HostState.ON)
				o2State = 3;
			else if (o2.getState() == Host.HostState.POWERING_ON)
				o2State = 2;
			else if (o2.getState() == Host.HostState.SUSPENDED)
				o2State = 1;
			else
				o2State = 0; //ranks off and transition states lowest
			
			return o1State - o2State;
		}
	};
	
	public static Comparator<Host> getComparator(final HostComparator... multipleOptions) {
        return new Comparator<Host>() {
            public int compare(Host o1, Host o2) {
                for (HostComparator option : multipleOptions) {
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
