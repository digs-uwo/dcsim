package edu.uwo.csd.dcsim2.host.resourcemanager;

import edu.uwo.csd.dcsim2.common.ObjectFactory;

/**
 * Constructs instances of SimpleStorageManager
 * 
 * @author Michael Tighe
 *
 */
public class SimpleStorageManagerFactory implements ObjectFactory<SimpleStorageManager> {

	@Override
	public SimpleStorageManager newInstance() {
		return new SimpleStorageManager();
	}

}
