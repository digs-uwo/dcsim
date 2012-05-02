package edu.uwo.csd.dcsim2.host.model;

import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.host.*;
import edu.uwo.csd.dcsim2.host.resourcemanager.*;
import edu.uwo.csd.dcsim2.host.power.*;
import edu.uwo.csd.dcsim2.host.scheduler.*;

public class ProLiantML110G4DualCoreHost extends Host {

	private static int nCpu = 1;
	private static int nCores = 2;
	private static int coreCapacity = 1860;
	private static int memory = 4096; //4GB
	private static int bandwidth = 131072 * 2; //1 Gb + 1Gb for management TODO poor assumption!
	private static long storage = 36864; //36GB
	private static HostPowerModel powerModel = new SPECHostPowerModel(10, 86, 89.4, 92.6, 96, 99.5, 102, 106, 108, 112, 114, 117);
	
	public ProLiantML110G4DualCoreHost(Simulation simulation,
			CpuManager cpuManager,
			MemoryManager memoryManager,
			BandwidthManager bandwidthManager,
			StorageManager storageManager,
			CpuScheduler cpuScheduler) {
		super(simulation, nCpu, nCores, coreCapacity, memory, bandwidth, storage, cpuManager, memoryManager, bandwidthManager, storageManager, cpuScheduler, powerModel);
	}
	
}
