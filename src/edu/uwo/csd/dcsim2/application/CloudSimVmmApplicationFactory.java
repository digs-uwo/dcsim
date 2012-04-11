package edu.uwo.csd.dcsim2.application;

public class CloudSimVmmApplicationFactory implements ApplicationFactory {

	@Override
	public Application createApplication() {
		return new CloudSimVmmApplication();
	}

	@Override
	public int getHeight() {
		return Integer.MAX_VALUE;
	}

}
