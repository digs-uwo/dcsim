package edu.uwo.csd.dcsim2.host.model;

import edu.uwo.csd.dcsim2.host.*;
import edu.uwo.csd.dcsim2.host.resourcemanager.*;
import edu.uwo.csd.dcsim2.host.power.*;
import edu.uwo.csd.dcsim2.host.scheduler.*;

public class ProLiantDL380G6EightCoreHost extends Host {

	private static int nCpu = 2;
	private static int nCores = 4;
	private static int coreCapacity = 2400;
	private static int memory = 8192; //8GB
	private static int bandwidth = 131072 * 2; //1 Gb + 1Gb for management TODO poor assumption!
	private static long storage = 36864; //36GB
	private static HostPowerModel powerModel = new SPECHostPowerModel(10, 63.7, 95.3, 109, 118, 125, 133, 142, 153, 164, 175, 187);
	
	public ProLiantDL380G6EightCoreHost(CpuManager cpuManager,
			MemoryManager memoryManager,
			BandwidthManager bandwidthManager,
			StorageManager storageManager,
			CpuScheduler cpuScheduler) {
		super(nCpu, nCores, coreCapacity, memory, bandwidth, storage, cpuManager, memoryManager, bandwidthManager, storageManager, cpuScheduler, powerModel);
	}
	
}
