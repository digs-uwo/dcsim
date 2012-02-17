package edu.uwo.csd.dcsim2.application;

public class WebServerTier extends ApplicationTier {

	public WebServerTier(WorkConsumer workTarget) {
		super(workTarget);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected WebServerApplication instantiateApplication() {
		return new WebServerApplication(this);
	}

}
