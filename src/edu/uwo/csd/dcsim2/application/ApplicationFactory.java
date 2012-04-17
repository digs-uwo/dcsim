package edu.uwo.csd.dcsim2.application;

public interface ApplicationFactory {

	public Application createApplication();
	public void startApplication(Application application);
	public void stopApplication(Application application);
	public int getHeight();
	
}
