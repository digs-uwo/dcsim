package edu.uwo.csd.dcsim.core;

public interface DaemonScheduler {

	public void start();
	
	public void start(long time);
	
	public void stop();
	
	public boolean isRunning();
	
	public boolean isEnabled();
	
	public void setEnabled(boolean enabled);
	
}
