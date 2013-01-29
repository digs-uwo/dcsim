package edu.uwo.csd.dcsim.host.scheduler;

import edu.uwo.csd.dcsim.common.ObjectFactory;

public class DefaultResourceSchedulerFactory implements ObjectFactory<DefaultResourceScheduler> {

	@Override
	public DefaultResourceScheduler newInstance() {
		return new DefaultResourceScheduler();
	}

}
