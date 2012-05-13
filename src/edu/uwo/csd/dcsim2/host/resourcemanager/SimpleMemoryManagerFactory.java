package edu.uwo.csd.dcsim2.host.resourcemanager;

import edu.uwo.csd.dcsim2.common.ObjectFactory;

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
