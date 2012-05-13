package edu.uwo.csd.dcsim2.host.resourcemanager;

import edu.uwo.csd.dcsim2.common.ObjectFactory;

/**
 * Constructs instances of SimpleCpuManager
 * 
 * @author Michael Tighe
 *
 */
public class SimpleCpuManagerFactory implements ObjectFactory<SimpleCpuManager> {

	@Override
	public SimpleCpuManager newInstance() {
		return new SimpleCpuManager();
	}

}
