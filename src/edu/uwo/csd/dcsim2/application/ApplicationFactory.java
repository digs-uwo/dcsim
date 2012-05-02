package edu.uwo.csd.dcsim2.application;

import edu.uwo.csd.dcsim2.core.Simulation;

public interface ApplicationFactory {

	public Application createApplication(Simulation simulation);
	public void startApplication(Application application);
	public void stopApplication(Application application);
	public int getHeight();
	
}
