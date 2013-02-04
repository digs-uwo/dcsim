package edu.uwo.csd.dcsim.application.events;

import edu.uwo.csd.dcsim.application.*;
import edu.uwo.csd.dcsim.core.Event;

public class ShutdownServiceEvent extends Event {

	private Service service;
	
	public ShutdownServiceEvent(ServiceProducer serviceProducer, Service service) {
		super(serviceProducer);
		this.service = service;
	}
	
	public Service getService() {
		return service;
	}

}
