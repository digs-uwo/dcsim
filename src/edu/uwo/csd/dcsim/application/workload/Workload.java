package edu.uwo.csd.dcsim.application.workload;

import edu.uwo.csd.dcsim.application.Application;
import edu.uwo.csd.dcsim.application.WorkProducer;
import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.core.events.DaemonRunEvent;
import edu.uwo.csd.dcsim.core.metrics.SlaViolationMetric;

/**
 * Represents an external workload submitting work to Applications running in the DataCentre.
 * 
 * @author Michael Tighe
 *
 */
public abstract class Workload implements SimulationEventListener, WorkProducer {
	
	protected Simulation simulation;
	private double totalWork = 0;
	private double completedWork = 0;
	private WorkProducer completedWorkSource; //final work producer that completes the work produced by this workload

	public Workload(Simulation simulation) {
		
		this.simulation = simulation;
		
		//schedule initial update event
		simulation.sendEvent(new DaemonRunEvent(this));
	}
	
	/**
	 * Get work awaiting processing
	 * @return
	 */
	protected abstract double getCurrentWorkLevel(); 
	
	public void advanceToCurrentTime() {
		//this is where we record the total work done over the interval
		double completedWorkLevel = 0;
		if (completedWorkSource != null) {
			completedWorkLevel = completedWorkSource.getWorkOutputLevel();
		}
		
		totalWork += getCurrentWorkLevel() * simulation.getElapsedSeconds();
		completedWork += completedWorkLevel * simulation.getElapsedSeconds();
	}
	
	/**
	 * Update metric values
	 */
	public void updateMetrics() {
		/*
		 * The denominator for SLA violation metrics is added at the Workload to prevent multiple tiers from adding the same incoming work
		 * more than once to the total (thus incorrectly reducing the SLA violation value)
		 */
		double work = getCurrentWorkLevel() * simulation.getElapsedSeconds();
		
		SlaViolationMetric.getMetric(simulation, Application.SLA_VIOLATION_METRIC).addWork(work);
		SlaViolationMetric.getMetric(simulation, Application.SLA_VIOLATION_UNDERPROVISION_METRIC).addWork(work);
		SlaViolationMetric.getMetric(simulation, Application.SLA_VIOLATION_MIGRATION_OVERHEAD_METRIC).addWork(work);
	}
	
	/**
	 * Update the current value of the workload level to reflect changes in workload. Return the
	 * time (ms) when the next change in workload will occur.
	 * @return Time (ms) when the next change in workload will occur.
	 */
	protected abstract long updateWorkLevel();
	
	/**
	 * Get the total amount of work that has been submitted by this Workload
	 * @return
	 */
	public double getTotalWork() {
		return totalWork;
	}
	
	/**
	 * Get the total amount of completed work received back by this Workload
	 * @return
	 */
	public double getCompletedWork() {
		return completedWork;
	}

	@Override
	public double getWorkOutputLevel() {
		return getCurrentWorkLevel();
	}
	
	public void setCompletedWorkSource(WorkProducer completedWorkSource) {
		this.completedWorkSource = completedWorkSource;
	}
	
	@Override
	public void handleEvent(Event e) {
		if (e instanceof DaemonRunEvent) {
			long nextEventTime = updateWorkLevel();
			if (nextEventTime > simulation.getSimulationTime()) {
				simulation.sendEvent(new DaemonRunEvent(this), nextEventTime);
			}
		}
	}

}
