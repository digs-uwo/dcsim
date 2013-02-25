package edu.uwo.csd.dcsim.management.policies;

import java.util.ArrayList;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.management.*;
import edu.uwo.csd.dcsim.management.capabilities.HostPoolManager;
import edu.uwo.csd.dcsim.management.events.HostStatusEvent;

public class HostStatusPolicy extends Policy {

	ArrayList<Class<? extends Event>> triggerEvents = new ArrayList<Class<? extends Event>>();
	
	private int windowSize;
	
	public HostStatusPolicy(int windowSize) {
		addRequiredCapability(HostPoolManager.class);
		
		this.windowSize = windowSize;
	}

	public void execute(HostStatusEvent event) {		
		HostPoolManager hostPool = manager.getCapability(HostPoolManager.class);
		
		hostPool.getHost(event.getHostStatus().getId()).addHostStatus(event.getHostStatus(), windowSize);
	}

	@Override
	public void onInstall() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onManagerStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onManagerStop() {
		// TODO Auto-generated method stub
		
	}

}
