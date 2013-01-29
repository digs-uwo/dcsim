package edu.uwo.csd.dcsim.application;

import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.core.metrics.SlaViolationMetric;
import edu.uwo.csd.dcsim.vm.VirtualResources;

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
	protected VirtualResources calculateResourcesRequired() {
		VirtualResources resourcesRequired = new VirtualResources();
		resourcesRequired.setMemory(memory);
		resourcesRequired.setStorage(storage);
		resourcesRequired.setBandwidth(bandwidth);
		
		//get current workload level from application tier and calculate CPU requirements
		double workLevel = applicationTier.getWorkLevel(this);
		
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
	public void scheduleResources(VirtualResources resourcesScheduled) {
		//check that memory, storage and bandwidth meet required minimum
		if (resourcesScheduled.getMemory() < memory ||
				resourcesScheduled.getStorage() < storage ||
						resourcesScheduled.getBandwidth() < bandwidth) {
			workOutputLevel = 0;
		} else {
			//we have enough memory, storage and bandwidth
			
			//first, subtract the overhead
			double cpuAvailableForWork = resourcesScheduled.getCpu() - cpuOverhead;
			
			//then divide by the amount of cpu required for each unit of work
			workOutputLevel = cpuAvailableForWork / cpuPerWork;
		}
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
		double workLevel = applicationTier.getWorkLevel(this);
		double slavUnderprovision = (workLevel - workOutputLevel) * simulation.getElapsedSeconds();
		double slavMig = 0; //TODO add this feature

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

	
	
}
