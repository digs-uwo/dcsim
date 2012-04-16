package edu.uwo.csd.dcsim2.host.model;

import edu.uwo.csd.dcsim2.application.CloudSimVmmApplicationFactory;
import edu.uwo.csd.dcsim2.host.Host;
import edu.uwo.csd.dcsim2.host.power.*;
import edu.uwo.csd.dcsim2.host.resourcemanager.*;
import edu.uwo.csd.dcsim2.host.scheduler.*;

public class CloudSimHost extends Host {

	private static HostPowerModel powerModel = new LinearHostPowerModel(175, 250);
	
	public CloudSimHost(int coreCapacity) {	
		//16GB RAM, 10Gb/s network (* 2 to assume separate network for migration), 1TB storage
		super(1, 1, coreCapacity, 16384, 1310720*2, 1048576, 
				new StaticOversubscribingCpuManager(0),
				new StaticMemoryManager(), 
				new StaticBandwidthManager(1310720), 
				new StaticStorageManager(), 
				new FairShareCpuScheduler(),
				powerModel,
				new CloudSimVmmApplicationFactory());
	}

	 
	
}
