package edu.uwo.csd.dcsim2.host.model;

import edu.uwo.csd.dcsim2.application.CloudSimVmmApplicationFactory;
import edu.uwo.csd.dcsim2.host.Host;
import edu.uwo.csd.dcsim2.host.power.*;
import edu.uwo.csd.dcsim2.host.resourcemanager.*;
import edu.uwo.csd.dcsim2.host.scheduler.*;

public class CloudSimHost extends Host {

	private static HostPowerModel powerModel = new LinearHostPowerModel(175, 250);
	
	public CloudSimHost(int coreCapacity) {		
		super(1, 1, coreCapacity, 8192, 131072, 1048576, 
				new StaticOversubscribingCpuManager(0),
				new StaticMemoryManager(), 
				new StaticBandwidthManager(131072), 
				new StaticStorageManager(), 
				new FairShareCpuScheduler(),
				powerModel,
				new CloudSimVmmApplicationFactory());
	}

	 
	
}
