package edu.uwo.csd.dcsim.application;

public interface ApplicationListener {

	public void onShutdownApplication(Application application);
	public void onCreateTaskInstance(TaskInstance taskInstance);
	public void onRemoveTaskInstance(TaskInstance taskInstance);
	
}
