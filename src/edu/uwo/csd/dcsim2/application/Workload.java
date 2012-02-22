package edu.uwo.csd.dcsim2.application;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim2.DCSim2;
import edu.uwo.csd.dcsim2.core.*;

public abstract class Workload extends SimulationEntity implements WorkConsumer {

	private static Logger logger = Logger.getLogger(Workload.class);
	
	public static final int WORKLOAD_UPDATE_WORKLEVEL_EVENT = 1;
	
	private static ArrayList<Workload> workloads = new ArrayList<Workload>();
	
	private int totalWork = 0;
	private int completedWork = 0;
	private WorkConsumer workTarget;
	
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

	protected abstract int retrievePendingWork(); 
	
	public void update() {
		if (workTarget != null && Simulation.getSimulation().getLastUpdate() < Simulation.getSimulation().getSimulationTime()) {
			int pendingWork = retrievePendingWork();
			totalWork += pendingWork;
			workTarget.addWork(pendingWork);
			logger.debug("Workload has " + pendingWork + " work units pending");
		}
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
