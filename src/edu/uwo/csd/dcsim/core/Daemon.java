package edu.uwo.csd.dcsim.core;

public interface Daemon {

	/**
	 * Called when the service is started
	 */
	public void onStart(Simulation simulation);
	
	/**
	 * Called when the service is to run
	 */
	public void run(Simulation simulation);
	
	/**
	 * Called when the service is stopped
	 */
	public void onStop(Simulation simulation);
	
}
