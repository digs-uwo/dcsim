package edu.uwo.csd.dcsim2.host.model;

import edu.uwo.csd.dcsim2.host.*;
import edu.uwo.csd.dcsim2.host.resourcemanager.*;
import edu.uwo.csd.dcsim2.host.power.*;
import edu.uwo.csd.dcsim2.host.scheduler.*;

public class ProLiantDL160G5E5420Host extends Host {

	private static int nCpu = 2;
	private static int nCores = 4;
	private static int coreCapacity = 2500;
	private static int memory = 16384; //16GB
	private static int bandwidth = 131072 * 2; //1 Gb + 1Gb for management TODO poor assumption!
	private static long storage = 36864; //36GB
	private static HostPowerModel powerModel = new SPECHostPowerModel(10, 148, 159, 167, 175, 184, 194, 204, 213, 220, 227, 233);
	
	public ProLiantDL160G5E5420Host(CpuManager cpuManager,
			MemoryManager memoryManager,
			BandwidthManager bandwidthManager,
			StorageManager storageManager,
			CpuScheduler cpuScheduler) {
		super(nCpu, nCores, coreCapacity, memory, bandwidth, storage, cpuManager, memoryManager, bandwidthManager, storageManager, cpuScheduler, powerModel);
	}
	
}
