package edu.uwo.csd.dcsim2.application;

public class VmmApplicationFactory implements ApplicationFactory {

	@Override
	public Application createApplication() {
		return new VmmApplication();
	}

	@Override
	public int getHeight() {
		return Integer.MAX_VALUE;
	}

}
