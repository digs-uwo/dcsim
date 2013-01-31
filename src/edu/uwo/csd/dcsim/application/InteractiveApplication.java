package edu.uwo.csd.dcsim.application;

import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.core.metrics.SlaViolationMetric;
import edu.uwo.csd.dcsim.host.Resources;

/**
 * Represents an Application that operates in an interactive, request/reply manner, such as a web server. Incoming work
 * are considered 'requests' which are processed by a specified amount of CPU and Bandwidth per work unit.
 * 
 * @author Michael Tighe
 *
 */
public class InteractiveApplication extends Application {

	private ApplicationTier applicationTier; //the tier which this Application belongs to
	
	private double workOutputLevel =  0;
	
	private double cpuPerWork; //the amount of CPU required to complete 1 work unit
	private double bandwidth; //fixed bandwidth usage
	private int memory; //fixed memory usage
	private long storage; //fixed storage usage
	private double cpuOverhead;

	double workLevel = 0;
	
	private Resources resourcesInUse = new Resources();
	
	private double slaUnderprovisionRate;
	private double slaMigrationPenaltyRate;
	
	private double totalIncomingWork = 0;
	private double totalSLAViolatedWork = 0;
	
	/**
	 * Create a new InteractiveApplication
	 * @param simulation
	 * @param applicationTier
	 * @param memory
	 * @param storage
	 * @param cpuPerWork
	 * @param bwPerWork
	 * @param cpuOverhead The amount of CPU required by the application even if there is no incoming work.
	 */
	public InteractiveApplication(Simulation simulation, ApplicationTier applicationTier, int memory, double bandwidth, long storage, double cpuPerWork, double cpuOverhead) {
		super(simulation);
		
		this.memory = memory;
		this.storage = storage;
		this.cpuPerWork = cpuPerWork;
		this.bandwidth = bandwidth;
		
		this.applicationTier = applicationTier;
		
		//overhead current consists only of a fixed CPU overhead added to the Applications resource use, even if there is no incoming work
		this.cpuOverhead = cpuOverhead;
	}

	@Override
	protected Resources calculateResourcesRequired() {
		Resources resourcesRequired = new Resources();
		resourcesRequired.setMemory(memory);
		resourcesRequired.setStorage(storage);
		resourcesRequired.setBandwidth(bandwidth);
		
		//get current workload level from application tier and calculate CPU requirements
		workLevel = applicationTier.getWorkLevel(this);
		
		//calculate cpu required
		double cpuRequired = workLevel * cpuPerWork + cpuOverhead;
		
		resourcesRequired.setCpu(cpuRequired);
		
		return resourcesRequired;
	}
	
	/**
	 * calculates work output level based on given scheduled resources
	 * TODO rename?
	 */
	@Override
	public void scheduleResources(Resources resourcesScheduled) {
		
		resourcesInUse = resourcesScheduled;
		
		//check that memory, storage and bandwidth meet required minimum
		if (resourcesScheduled.getMemory() < memory ||
				resourcesScheduled.getStorage() < storage ||
						resourcesScheduled.getBandwidth() < bandwidth) {
			workOutputLevel = 0;
			
			//since we are not performing any work due to insufficient resources, all incoming work is sla violated
			slaUnderprovisionRate = workLevel;
			slaMigrationPenaltyRate = 0;
		} else {
			//we have enough memory, storage and bandwidth
			
			//first, subtract the overhead
			double cpuAvailableForWork = resourcesScheduled.getCpu() - cpuOverhead;
			
			//then divide by the amount of cpu required for each unit of work
			workOutputLevel = cpuAvailableForWork / cpuPerWork;
			
			//calculate sla violation and migration penalty
			slaUnderprovisionRate = workLevel - workOutputLevel;
			
			//calculate migration penalty as a percentage (configurable) of the completed work
			if (vm.isMigrating()) {
				slaMigrationPenaltyRate = workOutputLevel * Double.parseDouble(Simulation.getProperty("vmMigrationSLAPenalty"));
			} else {
				slaMigrationPenaltyRate = 0;
			}
		}
	}
	
	@Override
	public void postScheduling() {
		//nothing to do
	}
	
	@Override
	public void execute() {
		//record work completed, total incoming, and total sla violation
		double workLevel = applicationTier.getWorkLevel(this);
		totalIncomingWork += workLevel * simulation.getElapsedSeconds();
		totalSLAViolatedWork += (workLevel - workOutputLevel) * simulation.getElapsedSeconds();
	}
	
	@Override
	public void updateMetrics() {
		double slavUnderprovision = slaUnderprovisionRate * simulation.getElapsedSeconds();
		double slavMig = slaMigrationPenaltyRate * simulation.getElapsedSeconds();

		//TODO perhaps only calculate migration overhead here, move underprovision to Workload, as if no applications are attached to a workload, then no SLA violation is added, even though no work is being performed
		SlaViolationMetric.getMetric(simulation, Application.SLA_VIOLATION_METRIC).addSlaVWork(slavUnderprovision + slavMig);
		SlaViolationMetric.getMetric(simulation, Application.SLA_VIOLATION_UNDERPROVISION_METRIC).addSlaVWork(slavUnderprovision);
		SlaViolationMetric.getMetric(simulation, Application.SLA_VIOLATION_MIGRATION_OVERHEAD_METRIC).addSlaVWork(slavMig);
	}

	@Override
	public double getWorkOutputLevel() {
		return workOutputLevel;
	}

	@Override
	public double getTotalIncomingWork() {
		return totalIncomingWork;
	}

	@Override
	public double getTotalSLAViolatedWork() {
		return totalSLAViolatedWork;
	}

	@Override
	public Resources getResourcesInUse() {
		return resourcesInUse;
	}

	@Override
	public double getSLAUnderprovisionRate() {
		return slaUnderprovisionRate;
	}

	@Override
	public double getSLAMigrationPenaltyRate() {
		return slaMigrationPenaltyRate;
	}



	
	
}
