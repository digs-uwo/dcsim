package edu.uwo.csd.dcsim2.application;

public abstract class ApplicationTier implements WorkProducer, WorkConsumer {

	private WorkProducer workSource;
	private WorkConsumer workTarget;
	private LoadBalancer loadBalancer;
	
	private int outgoingWork;
	
	public ApplicationTier(WorkProducer workSource, WorkConsumer workTarget) {
		outgoingWork = 0;
		this.workSource = workSource;
		this.workTarget = workTarget;
	}

	public abstract Application createApplication();

	public int getDepth() {
		int depth = 1;
		
		WorkProducer parent = workSource;
		while (parent instanceof ApplicationTier) {
			++depth;
			parent = ((ApplicationTier)parent).getWorkSource();
		}
		return depth;
	}

	public WorkProducer getWorkSource() {
		return workSource;
	}
	
	public WorkConsumer getWorkTarget() {
		return workTarget;
	}
	
	@Override
	public void addWork(int work) {
		outgoingWork += work;
	}

	@Override
	public int retrieveWork() {
		int out = outgoingWork;
		outgoingWork = 0;
		return out;
	}
	
	
	
}
