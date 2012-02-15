package edu.uwo.csd.dcsim2.application;

public abstract class Workload implements WorkProducer, WorkConsumer {

	private int pendingWork;
	private int totalWork;
	private int completedWork;
	
	
	
	@Override
	public void addWork(int work) {
		completedWork += work;
	}

	@Override
	public int retrieveWork() {
		int out = pendingWork;
		pendingWork = 0;
		return out;
	}

}
