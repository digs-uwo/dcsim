package edu.uwo.csd.dcsim2.application;

import java.util.ArrayList;

import edu.uwo.csd.dcsim2.core.*;

public abstract class Workload extends SimulationEntity implements WorkConsumer {

	public static final int WORKLOAD_UPDATE_WORKLEVEL_EVENT = 1;
	
	private static ArrayList<Workload> workloads = new ArrayList<Workload>();
	
	private int totalWork = 0;
	private int completedWork = 0;
	private WorkConsumer workTarget;
	private long lastUpdateTime = 0;
	
	public static void updateAllWorkloads() {
		for (Workload workload : workloads) {
			workload.update();
		}
	}
	
	public Workload() {
		workloads.add(this);
	}
	
	@Override
	public void addWork(int work) {
		completedWork += work;
	}

	protected abstract int retrievePendingWork(long lastUpdateTime); 
	
	public void update() {
		if (workTarget != null && lastUpdateTime < Simulation.getSimulation().getSimulationTime()) {
			int pendingWork = retrievePendingWork(lastUpdateTime);
			totalWork += pendingWork;
			workTarget.addWork(pendingWork);
		}
		lastUpdateTime = Simulation.getSimulation().getSimulationTime();
	}
	
	/**
	 * Update the current value of the workload level to reflect changes in workload. Return the
	 * time (ms) when the next change in workload will occur.
	 * @return Time (ms) when the next change in workload will occur.
	 */
	protected abstract long updateWorkLevel();
	
	public int getTotalWork() {
		return totalWork;
	}
	
	public int getCompletedWork() {
		return completedWork;
	}
	
	public WorkConsumer getWorkTarget() {
		return workTarget;
	}
	
	public void setWorkTarget(WorkConsumer workTarget) {
		this.workTarget = workTarget;
	}
	
	@Override
	public void handleEvent(Event e) {
		if (e.getType() == Workload.WORKLOAD_UPDATE_WORKLEVEL_EVENT) {
			long nextEventTime = updateWorkLevel();
			if (nextEventTime > Simulation.getSimulation().getSimulationTime()) {
				Simulation.getSimulation().sendEvent(
						new Event(Workload.WORKLOAD_UPDATE_WORKLEVEL_EVENT, nextEventTime, this, this));
			}
		}
	}

}
