package edu.uwo.csd.dcsim.application.workload;

import edu.uwo.csd.dcsim.application.Application;
import edu.uwo.csd.dcsim.application.WorkProducer;
import edu.uwo.csd.dcsim.common.Utility;
import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.core.metrics.SlaViolationMetric;

/**
 * Represents an external workload submitting work to Applications running in the DataCentre.
 * 
 * @author Michael Tighe
 *
 */
public abstract class Workload implements SimulationEventListener, WorkProducer {
	
	public static final int WORKLOAD_UPDATE_WORKLEVEL_EVENT = 1;
	
	protected Simulation simulation;
	private double totalWork = 0;
	private double completedWork = 0;
	private double currentWorkLevel = 0;
	private WorkProducer workSource; //final work producer that completes the work produced by this workload

	public Workload(Simulation simulation) {
		
		this.simulation = simulation;
		
		//schedule initial update event
		simulation.sendEvent(
				new Event(Workload.WORKLOAD_UPDATE_WORKLEVEL_EVENT, simulation.getSimulationTime(), this, this));
	}
	
	/**
	 * Get work awaiting processing
	 * @return
	 */
	protected abstract double getCurrentWorkLevel(); 
	
	/**
	 * Update the amount of work that must be processed in the current interval of time being simulated
	 */
	public void update() {
		
		//ensure that new work is only calculated once per simulation time interval 
		currentWorkLevel = getCurrentWorkLevel();
		currentWorkLevel = Utility.roundDouble(currentWorkLevel); //correct for precision errors by rounding TODO still necessary?

//		if (simulation.isRecordingMetrics()) {
//			totalWork += currentWorkLevel;
//		}
		
	}
	
	/**
	 * Update metric values
	 */
	public void updateMetrics() {
		
		//TODO this is where we calculate the total work done over the interval
		
		/*
		 * The denominator for SLA violation metrics is added at the Workload to prevent multiple tiers from adding the same incoming work
		 * more than once to the total (thus incorrectly reducing the SLA violation value)
		 */
//		SlaViolationMetric.getMetric(simulation, Application.SLA_VIOLATION_METRIC).addWork(currentWork);
//		SlaViolationMetric.getMetric(simulation, Application.SLA_VIOLATION_UNDERPROVISION_METRIC).addWork(currentWork);
//		SlaViolationMetric.getMetric(simulation, Application.SLA_VIOLATION_MIGRATION_OVERHEAD_METRIC).addWork(currentWork);
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
		return currentWorkLevel;
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
