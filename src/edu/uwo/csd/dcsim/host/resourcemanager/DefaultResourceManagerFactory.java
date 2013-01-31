package edu.uwo.csd.dcsim.host.resourcemanager;

import edu.uwo.csd.dcsim.common.ObjectFactory;

public class DefaultResourceManagerFactory implements ObjectFactory<DefaultResourceManager> {

	@Override
	public DefaultResourceManager newInstance() {
		return new DefaultResourceManager();
	}

}
