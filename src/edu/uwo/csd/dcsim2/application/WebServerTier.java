package edu.uwo.csd.dcsim2.application;

public class WebServerTier extends ApplicationTier {

	private int memory;
	private long storage;
	
	public WebServerTier(int memory, long storage) {
		this.memory = memory;
		this.storage = storage;
	}
	
	@Override
	protected WebServerApplication instantiateApplication() {
		return new WebServerApplication(this, memory, storage);
	}

}
