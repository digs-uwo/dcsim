package edu.uwo.csd.dcsim.management.stub;

import java.util.Comparator;

/**
 * Compares VM stubs by a (non-empty) series of attributes or factors. The 
 * available factors are:
 * 
 * + CPU_CORES:		requested number of cores;
 * + CORE_CAP:		requested core capacity;
 * + MEMORY:		requested memory;
 * + BANDWIDTH:		requested bandwidth;
 * + CPU_IN_USE:	current CPU in use.
 * 
 * @author Gaston Keller
 *
 */
public enum VmStubComparator implements Comparator<VmStub> {

	CPU_CORES {
		public int compare(VmStub o1, VmStub o2) {
			return o1.getVM().getVMDescription().getCores() - o2.getVM().getVMDescription().getCores();
		}
	},
	CORE_CAP {
		public int compare(VmStub o1, VmStub o2) {
			return o1.getVM().getVMDescription().getCoreCapacity() - o2.getVM().getVMDescription().getCoreCapacity();
		}
	},
	MEMORY {
		public int compare(VmStub o1, VmStub o2) {
			return o1.getVM().getVMDescription().getMemory() - o2.getVM().getVMDescription().getMemory();
		}
	},
	BANDWIDTH {
		public int compare(VmStub o1, VmStub o2) {
			return o1.getVM().getVMDescription().getBandwidth() - o2.getVM().getVMDescription().getBandwidth();
		}
	},
	CPU_IN_USE {
		public int compare(VmStub o1, VmStub o2) {
			double compare = o1.getCpuInUse() - o2.getCpuInUse(); 
			if (compare < 0)
				return -1;
			else if (compare > 0)
				return 1;
			return 0;
		}
	};
	
	public static Comparator<VmStub> getComparator(final VmStubComparator... multipleOptions) {
        return new Comparator<VmStub>() {
            public int compare(VmStub o1, VmStub o2) {
                for (VmStubComparator option : multipleOptions) {
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
