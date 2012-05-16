package edu.uwo.csd.dcsim.host.resourcemanager;

import edu.uwo.csd.dcsim.common.ObjectFactory;

/**
 * Constructs instances of OversubscribingCpuManager
 * 
 * @author Michael Tighe
 *
 */
public class OversubscribingCpuManagerFactory implements ObjectFactory<OversubscribingCpuManager> {

	@Override
	public OversubscribingCpuManager newInstance() {
		return new OversubscribingCpuManager();
	}

}
