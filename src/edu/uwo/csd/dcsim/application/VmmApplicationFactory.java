package edu.uwo.csd.dcsim.application;

import edu.uwo.csd.dcsim.core.Simulation;

/**
 * Instantiates VmmApplications
 * @author Michael Tighe
 *
 */
public class VmmApplicationFactory implements ApplicationFactory {

	@Override
	public Application createApplication(Simulation simulation) {
		return new VmmApplication(simulation);
	}

	@Override
	public void startApplication(Application application) {
		//nothing to do
	}

	@Override
	public void stopApplication(Application application) {
		//nothing to do
	}

	@Override
	public int getDepth() {
		//depth of this application is always 0, as it will never be part of a multi-tiered application
		return 0;
	}

}
