package edu.uwo.csd.dcsim2.application.workload;

import edu.uwo.csd.dcsim2.application.WorkConsumer;
import edu.uwo.csd.dcsim2.core.*;

public abstract class Workload implements SimulationEventListener, WorkConsumer {
	
	public static final int WORKLOAD_UPDATE_WORKLEVEL_EVENT = 1;
	
	protected Simulation simulation;
	private double totalWork = 0;
	private double completedWork = 0;
	private WorkConsumer workTarget;
	
	
	
	public Workload(Simulation simulation) {
		
		this.simulation = simulation;
		
		//schedule initial update event
		simulation.sendEvent(
				new Event(Workload.WORKLOAD_UPDATE_WORKLEVEL_EVENT, 0, this, this));
	}
	
	@Override
	public void addWork(double work) {
		if (simulation.isRecordingMetrics()) {
			completedWork += work;
			completedWork = Utility.roundDouble(completedWork); //correct for precision errors by rounding
		}
	}

	protected abstract double retrievePendingWork(); 
	
	public void update() {
		if (workTarget != null && simulation.getLastUpdate() < simulation.getSimulationTime()) {
			double pendingWork = retrievePendingWork();
			pendingWork = Utility.roundDouble(pendingWork); //correct for precision errors by rounding

			if (simulation.isRecordingMetrics()) {
				totalWork += pendingWork;
				totalWork = Utility.roundDouble(totalWork); //correct for precision errors by rounding
			}
			
			workTarget.addWork(pendingWork);
		}
	}
	
	/**
	 * Update the current value of the workload level to reflect changes in workload. Return the
	 * time (ms) when the next change in workload will occur.
	 * @return Time (ms) when the next change in workload will occur.
	 */
	protected abstract long updateWorkLevel();
	
	public double getTotalWork() {
		return totalWork;
	}
	
	public double getCompletedWork() {
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
			if (nextEventTime > simulation.getSimulationTime()) {
				simulation.sendEvent(
						new Event(Workload.WORKLOAD_UPDATE_WORKLEVEL_EVENT, nextEventTime, this, this));
			}
		}
	}

}
