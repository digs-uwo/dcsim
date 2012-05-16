package edu.uwo.csd.dcsim.host.resourcemanager;

import edu.uwo.csd.dcsim.common.ObjectFactory;

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
