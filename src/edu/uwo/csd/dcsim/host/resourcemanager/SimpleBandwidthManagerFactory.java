package edu.uwo.csd.dcsim.host.resourcemanager;

import edu.uwo.csd.dcsim.common.ObjectFactory;

/**
 * Constructs instances of SimpleBandwidthManager
 * 
 * @author Michael Tighe
 *
 */
public class SimpleBandwidthManagerFactory implements ObjectFactory<SimpleBandwidthManager> {

	@Override
	public SimpleBandwidthManager newInstance() {
		return new SimpleBandwidthManager();
	}

}
