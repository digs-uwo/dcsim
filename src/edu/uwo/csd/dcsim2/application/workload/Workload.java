package edu.uwo.csd.dcsim2.application.workload;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim2.application.WorkConsumer;
import edu.uwo.csd.dcsim2.core.*;

public abstract class Workload extends SimulationEntity implements WorkConsumer {

	private static Logger logger = Logger.getLogger(Workload.class);
	
	public static final int WORKLOAD_UPDATE_WORKLEVEL_EVENT = 1;
	
	private static ArrayList<Workload> workloads = new ArrayList<Workload>();
	
	private double totalWork = 0;
	private double completedWork = 0;
	private WorkConsumer workTarget;
	
	public static void updateAllWorkloads() {
		for (Workload workload : workloads) {
			workload.update();
		}
	}
	
	public static void logAllWorkloads() {
		for (Workload workload : workloads) {
			workload.logCompleted();
		}
	}
	
	public Workload() {
		//add to global list of workloads
		workloads.add(this);
		
		//schedule initial update event
		Simulation.getSimulation().sendEvent(
				new Event(Workload.WORKLOAD_UPDATE_WORKLEVEL_EVENT, 0, this, this));
	}
	
	@Override
	public void addWork(double work) {
		completedWork += work;
		
		completedWork = Utility.roundDouble(completedWork); //correct for precision errors by rounding
	}

	protected abstract double retrievePendingWork(); 
	
	public void update() {
		if (workTarget != null && Simulation.getSimulation().getLastUpdate() < Simulation.getSimulation().getSimulationTime()) {
			double pendingWork = retrievePendingWork();
			pendingWork = Utility.roundDouble(pendingWork); //correct for precision errors by rounding
			
			totalWork += pendingWork;
			totalWork = Utility.roundDouble(totalWork); //correct for precision errors by rounding
			
			workTarget.addWork(pendingWork);
			//logger.debug("Workload has " + pendingWork + " work units pending");
		}
	}
	
	public void logCompleted() {
		logger.info("Workload Total [" + Utility.roundDouble(completedWork, 2) + "/" + Utility.roundDouble(totalWork, 2) + "] work units");
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
			if (nextEventTime > Simulation.getSimulation().getSimulationTime()) {
				Simulation.getSimulation().sendEvent(
						new Event(Workload.WORKLOAD_UPDATE_WORKLEVEL_EVENT, nextEventTime, this, this));
			}
		}
	}

}
