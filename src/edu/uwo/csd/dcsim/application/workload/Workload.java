package edu.uwo.csd.dcsim.application.workload;

import edu.uwo.csd.dcsim.application.Application;
import edu.uwo.csd.dcsim.application.WorkConsumer;
import edu.uwo.csd.dcsim.common.Utility;
import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.core.metrics.FractionalMetric;

/**
 * Represents an external workload submitting work to Applications running in the DataCentre.
 * 
 * @author Michael Tighe
 *
 */
public abstract class Workload implements SimulationEventListener, WorkConsumer {
	
	public static final int WORKLOAD_UPDATE_WORKLEVEL_EVENT = 1;
	
	protected Simulation simulation;
	private double totalWork = 0;
	private double completedWork = 0;
	private double currentWork = 0;
	private WorkConsumer workTarget;

	public Workload(Simulation simulation) {
		
		this.simulation = simulation;
		
		//schedule initial update event
		simulation.sendEvent(
				new Event(Workload.WORKLOAD_UPDATE_WORKLEVEL_EVENT, simulation.getSimulationTime(), this, this));
	}
	
	@Override
	/**
	 * Record work completed by the Application(s)
	 */
	public void addWork(double work) {
		if (simulation.isRecordingMetrics()) {
			completedWork += work;
			completedWork = Utility.roundDouble(completedWork); //correct for precision errors by rounding
		}
	}

	/**
	 * Get work awaiting processing
	 * @return
	 */
	protected abstract double retrievePendingWork(); 
	
	/**
	 * Update the amount of work that must be processed in the current interval of time being simulated
	 */
	public void update() {
		
		//ensure that new work is only calculated once per simulation time interval 
		if (workTarget != null && simulation.getLastUpdate() < simulation.getSimulationTime()) {
			double pendingWork = retrievePendingWork();
			pendingWork = Utility.roundDouble(pendingWork); //correct for precision errors by rounding

			if (simulation.isRecordingMetrics()) {
				totalWork += pendingWork;
				totalWork = Utility.roundDouble(totalWork); //correct for precision errors by rounding
			}
			
			workTarget.addWork(pendingWork);
			
			currentWork = pendingWork;			
		}
	}
	
	/**
	 * Update metric values
	 */
	public void updateMetrics() {
		
		/*
		 * The denominator for SLA violation metrics is added at the Workload to prevent multiple tiers from adding the same incoming work
		 * more than once to the total (thus incorrectly reducing the SLA violation value)
		 */
		FractionalMetric.getSimulationMetric(simulation, Application.SLA_VIOLATION_METRIC).addDenominator(currentWork);
		FractionalMetric.getSimulationMetric(simulation, Application.SLA_VIOLATION_UNDERPROVISION_METRIC).addDenominator(currentWork);
		FractionalMetric.getSimulationMetric(simulation, Application.SLA_VIOLATION_MIGRATION_OVERHEAD_METRIC).addDenominator(currentWork);
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
	
	/**
	 * Get the target that this Workload submits work to
	 * @return
	 */
	public WorkConsumer getWorkTarget() {
		return workTarget;
	}
	
	/**
	 * Set the target that this Workload submits work to
	 * @param workTarget
	 */
	public void setWorkTarget(WorkConsumer workTarget) {
		this.workTarget = workTarget;
	}
	
	/**
	 * Get the current amount of work
	 * @return
	 */
	public double getCurrentWork() {
		return currentWork;
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
