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

	//variables to keep track of resource demand and consumption
	VirtualResources resourceDemand;		//the current level of resource demand / second
	VirtualResources resourceInUse;			//the current level of resource use  / second
	
	VirtualResources resourcesDemanded; 	//the amount of resources demanded in the last period
	VirtualResources resourcesUsed;			//the amount of resources used in the last period
	
	VirtualResources totalResourceDemand;	//the total amount of resources required since the application started
	VirtualResources totalResourceUsed;		//the total amount of resources used since the application started

	private double workRemaining = 0; //amount of work remaining to be processed
	private ApplicationTier applicationTier; //the tier which this Application belongs to
	private VirtualResources overhead; //the amount of overhead per second this application creates
	private VirtualResources overheadRemaining; //the amount of overhead accumulated over the elapsed period that remains to be processed
	
	private double incomingWork = 0; //amount of current incoming work
	private double totalIncomingWork = 0; //total incoming work during the simulation
	private double slaViolatedWork = 0; //amount of current work for which SLA is violated
	private double totalSlaViolatedWork = 0; //total amount of work for which SLA is violated
	private double migrationPenalty = 0; //the current SLA penalty due to migration
	private double totalMigrationPenalty = 0; //the total SLA penalty due to migration during the simulation
	
	private double cpuPerWork; //the amount of CPU required to complete 1 work
	private double bwPerWork; //the amount of bandwdith required to complete 1 work
	private int memory; //fixed memory usage
	private long storage; //fixed storage usage
	
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
	public InteractiveApplication(Simulation simulation, ApplicationTier applicationTier, int memory, long storage, double cpuPerWork, double bwPerWork, double cpuOverhead) {
		super(simulation);
		
		this.memory = memory;
		this.storage = storage;
		this.cpuPerWork = cpuPerWork;
		this.bwPerWork = bwPerWork;
		
		//initialize resource demand/consumption values
		resourceDemand = new VirtualResources();
		resourceInUse = new VirtualResources();
		resourcesDemanded = new VirtualResources();
		resourcesUsed = new VirtualResources();
		totalResourceDemand = new VirtualResources();
		totalResourceUsed = new VirtualResources();
		
		this.applicationTier = applicationTier;
		
		//overhead current consists only of a fixed CPU overhead added to the Applications resource use, even if there is no incoming work
		overhead = new VirtualResources();
		overhead.setCpu(cpuOverhead);
	}
	
	@Override
	public void prepareExecution() {
		//reset the resource demand and consumption values for the current interval
		resourcesDemanded = new VirtualResources();
		resourcesUsed = new VirtualResources();
		
		//calculate overhead for scheduling period
		overheadRemaining = new VirtualResources();
		
		overheadRemaining.setCpu(overhead.getCpu() * simulation.getElapsedSeconds());
		overheadRemaining.setBandwidth(overhead.getBandwidth() * simulation.getElapsedSeconds());
		overheadRemaining.setMemory(overhead.getMemory());
		overheadRemaining.setStorage(overhead.getStorage());
		
		//application overhead is included in resourceDemand
		resourcesDemanded = resourcesDemanded.add(overheadRemaining);
		
		//set up sla metrics
		incomingWork = 0;
		slaViolatedWork = 0;
		migrationPenalty = 0;
	}

	@Override
	public void updateResourceDemand() {
		//retrieve incoming work
		double incomingWork = applicationTier.retrieveWork(this);
		workRemaining += incomingWork;
		this.incomingWork += incomingWork;
		
		//if there is incoming work, calculate the resources required to perform it and add it to resourceDemand
		if (incomingWork > 0) {
			resourcesDemanded.setCpu(resourcesDemanded.getCpu() + (incomingWork * cpuPerWork));
			resourcesDemanded.setBandwidth(resourcesDemanded.getBandwidth() + (incomingWork * bwPerWork));
			resourcesDemanded.setMemory(memory);
			resourcesDemanded.setStorage(storage);
		}
	}
	
	@Override
	public VirtualResources execute(VirtualResources resourcesAvailable) {

		VirtualResources resourcesConsumed = new VirtualResources();
		
		//first ensure that all remaining overhead for the elapsed period has been processed
		if (overheadRemaining.getCpu() > 0) {
			if (resourcesAvailable.getCpu() > overheadRemaining.getCpu()) {
				//we have enough cpu to complete processing the overhead
				resourcesAvailable.setCpu(resourcesAvailable.getCpu() - overheadRemaining.getCpu());
				resourcesConsumed.setCpu(overheadRemaining.getCpu());
				overheadRemaining.setCpu(0);
			} else {
				//we do not have enough cpu to complete processing the overhead
				overheadRemaining.setCpu(overheadRemaining.getCpu() - resourcesAvailable.getCpu());
				resourcesConsumed.setCpu(resourcesAvailable.getCpu());
				resourcesAvailable.setCpu(0);
			}
		}
		if (overheadRemaining.getBandwidth() > 0) {
			if (resourcesAvailable.getBandwidth() > overheadRemaining.getBandwidth()) {
				//we have enough bandwidth to complete processing the overhead
				resourcesAvailable.setBandwidth(resourcesAvailable.getBandwidth() - overheadRemaining.getBandwidth());
				resourcesConsumed.setBandwidth(overheadRemaining.getBandwidth());
				overheadRemaining.setBandwidth(0);
			} else {
				//we do not have enough bandwidth to complete processing the overhead
				overheadRemaining.setBandwidth(overheadRemaining.getBandwidth() - resourcesAvailable.getBandwidth());
				resourcesConsumed.setBandwidth(resourcesAvailable.getBandwidth());
				resourcesAvailable.setBandwidth(0);
			}
		}
		
		resourcesConsumed.setMemory(overheadRemaining.getMemory());
		resourcesConsumed.setStorage(overheadRemaining.getStorage());
		
		//check minimum memory and storage. If not met, assume the application does not run. TODO is this correct? Should we use what we can? How would this affect application performance?
		if (resourcesAvailable.getMemory() < overheadRemaining.getMemory() || resourcesAvailable.getStorage() < overheadRemaining.getStorage()) {
			simulation.getLogger().info("Application has insufficient memory or storage to meet overhead requirements");
			return new VirtualResources(); //no resources consumed
		}
		
		/* 
		 * Process actual work
		 * 
		 * total work completed depends on CPU and BW. Calculate the
		 * amount of work possible for each assuming the other is infinite,
		 * and the minimum of the two is the amount of work completed
		 */
		
		double cpuWork, bwWork;
		
		if (cpuPerWork != 0)
			cpuWork = resourcesAvailable.getCpu() / cpuPerWork;
		else
			cpuWork = Double.MAX_VALUE;
		
		if (bwPerWork != 0)
			bwWork = resourcesAvailable.getBandwidth() / bwPerWork;
		else
			bwWork = Double.MAX_VALUE;
		
		double workCompleted = Math.min(cpuWork, bwWork);
		workCompleted = Math.min(workCompleted, workRemaining);
		
		if (workCompleted > workRemaining)
			throw new IllegalStateException("Application class " + this.getClass().getName() + " performed more work than was available to perform. Programming error.");
		
		applicationTier.getWorkTarget().addWork(workCompleted);
		workRemaining -= workCompleted;
	
		//compute total consumed resources
		resourcesConsumed.setCpu(resourcesConsumed.getCpu() + (workCompleted * cpuPerWork));
		resourcesConsumed.setBandwidth(resourcesConsumed.getBandwidth() + (workCompleted * bwPerWork));
		resourcesConsumed.setMemory(memory);
		resourcesConsumed.setStorage(storage);
		
		//add resourcesConsumed to resourcesInUse, which is keeping track of all resources used during this time interval
		resourcesUsed = resourcesUsed.add(resourcesConsumed);
		
		return resourcesConsumed;
	}


	@Override
	public void completeExecution() {
		
		//convert resourceDemand and resourceInUse to a 'resource per second' value by dividing by seconds elapsed in time interval
		resourceDemand = new VirtualResources();
		resourceDemand.setCpu(resourcesDemanded.getCpu() / (simulation.getElapsedSeconds()));
		resourceDemand.setBandwidth(resourcesDemanded.getBandwidth() / (simulation.getElapsedSeconds()));
		resourceDemand.setMemory(resourcesDemanded.getMemory());
		resourceDemand.setStorage(resourcesDemanded.getStorage());
		
		resourceInUse = new VirtualResources();
		resourceInUse.setCpu(resourcesUsed.getCpu() / (simulation.getElapsedSeconds()));
		resourceInUse.setBandwidth(resourcesUsed.getBandwidth() / (simulation.getElapsedSeconds()));
		resourceInUse.setMemory(resourcesUsed.getMemory());
		resourceInUse.setStorage(resourcesUsed.getStorage());
		
		slaViolatedWork = workRemaining;
		
		//add a migration penalty, if the VM is migrating
		if (vm.isMigrating()) {
			migrationPenalty += (incomingWork - workRemaining) * Double.parseDouble(Simulation.getProperty("vmMigrationSLAPenalty"));
			slaViolatedWork += migrationPenalty;
		}
		
		totalIncomingWork += incomingWork;
		totalSlaViolatedWork += slaViolatedWork;
		totalMigrationPenalty += migrationPenalty;
		
		//clear work remaining (i.e. drop requests that could not be fulfilled)
		workRemaining = 0;
	}
	
	@Override
	public void updateMetrics() {
		
		//add resource demand and use for this time interval to total values
		totalResourceDemand = totalResourceDemand.add(resourcesDemanded);
		totalResourceUsed = totalResourceUsed.add(resourcesUsed);
		
		/**
		 * Add values to the SLA violation numerators only. The denominator (total incoming requests) is populated by Workload objects, to prevent 
		 * tiers of a multi-tiered application from counting the same incoming work unit multiple times
		 */
		SlaViolationMetric.getMetric(simulation, Application.SLA_VIOLATION_METRIC).addSlaVWork(slaViolatedWork);
		SlaViolationMetric.getMetric(simulation, Application.SLA_VIOLATION_UNDERPROVISION_METRIC).addSlaVWork(slaViolatedWork - migrationPenalty);
		SlaViolationMetric.getMetric(simulation, Application.SLA_VIOLATION_MIGRATION_OVERHEAD_METRIC).addSlaVWork(migrationPenalty);

	}

	/**
	 * Get the amount of fixed overhead
	 * @return
	 */
	public VirtualResources getOverhead() {
		return overhead;
	}
	
	/**
	 * Set the amount of fixed overhead
	 * @param overhead
	 */
	public void setOverhead(VirtualResources overhead) {
		this.overhead = overhead;
	}
	
	@Override
	public VirtualResources getResourceDemand() {
		return resourceDemand;
	}

	@Override
	public VirtualResources getResourceInUse() {
		return resourceInUse;
	}

	@Override
	public VirtualResources getTotalResourceDemand() {
		return totalResourceDemand;
	}

	@Override
	public VirtualResources getTotalResourceUsed() {
		return totalResourceUsed;
	}
	
	@Override
	public double getSLAViolation() {
		return slaViolatedWork / incomingWork;
	}

	@Override
	public double getTotalSLAViolation() {
		return totalSlaViolatedWork / totalIncomingWork;
	}
	
	@Override
	public double getSLAViolatedWork() {
		return slaViolatedWork;
	}

	@Override
	public double getTotalSLAViolatedWork() {
		return totalSlaViolatedWork;
	}
	
	@Override	
	public double getMigrationPenalty() {
		return migrationPenalty;
	}
	
	@Override	
	public double getTotalMigrationPenalty() {
		return totalMigrationPenalty;
	}

	@Override
	public double getIncomingWork() {
		return incomingWork;
	}

	@Override
	public double getTotalIncomingWork() {
		return totalIncomingWork;
	}
	
	public double getWork(){
		return incomingWork;
	}
	
}
