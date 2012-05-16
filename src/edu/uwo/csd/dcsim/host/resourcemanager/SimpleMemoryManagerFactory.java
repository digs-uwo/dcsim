package edu.uwo.csd.dcsim.host.resourcemanager;

import edu.uwo.csd.dcsim.common.ObjectFactory;

/**
 * Constructs instances of SimpleMemoryManager
 * 
 * @author Michael Tighe
 *
 */
public class SimpleMemoryManagerFactory implements ObjectFactory<SimpleMemoryManager> {

	@Override
	public SimpleMemoryManager newInstance() {
		return new SimpleMemoryManager();
	}

}
