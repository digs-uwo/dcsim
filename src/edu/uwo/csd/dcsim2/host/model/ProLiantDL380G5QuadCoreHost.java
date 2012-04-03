package edu.uwo.csd.dcsim2.host.model;

import edu.uwo.csd.dcsim2.host.*;
import edu.uwo.csd.dcsim2.host.resourcemanager.*;
import edu.uwo.csd.dcsim2.host.power.*;
import edu.uwo.csd.dcsim2.host.scheduler.*;

public class ProLiantDL380G5QuadCoreHost extends Host {

	private static int nCpu = 2;
	private static int nCores = 2;
	private static int coreCapacity = 3000;
	private static int memory = 8192; //8GB
	private static int bandwidth = 131072 * 2; //1 Gb + 1Gb for management TODO poor assumption!
	private static long storage = 36864; //36GB
	private static HostPowerModel powerModel = new SPECHostPowerModel(0, 172, 177, 182, 187, 195, 205, 218, 229, 242, 252, 258);
	
	public ProLiantDL380G5QuadCoreHost(CpuManager cpuManager,
			MemoryManager memoryManager,
			BandwidthManager bandwidthManager,
			StorageManager storageManager,
			CpuScheduler cpuScheduler) {
		super(nCpu, nCores, coreCapacity, memory, bandwidth, storage, cpuManager, memoryManager, bandwidthManager, storageManager, cpuScheduler, powerModel);
	}
	
}
