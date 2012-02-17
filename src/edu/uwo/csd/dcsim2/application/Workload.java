package edu.uwo.csd.dcsim2.application;

public abstract class Workload implements WorkConsumer {

	private int totalWork;
	private int completedWork;
	private WorkConsumer workTarget;
	
	@Override
	public void addWork(int work) {
		completedWork += work;
	}

	protected abstract int retrievePendingWork(); 
	
	public void update() {
		int pendingWork = retrievePendingWork();
		totalWork += pendingWork;
		workTarget.addWork(pendingWork);
	}
	
	public int getTotalWork() {
		return totalWork;
	}
	
	public int getCompletedWork() {
		return completedWork;
	}

}
