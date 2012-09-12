package edu.uwo.csd.dcsim;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import edu.uwo.csd.dcsim.application.workload.Workload;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.host.Host;

/**
 * DataCentreSimulation is a simulation of a data centre, which consists of a collection of DataCentres containing Hosts, which
 * host VMs running Applications. DataCentreSimulation is the main object that controls the execution of the simulation.
 * 
 * All DataCentres in the simulation must be added to this object, as well as all Workload objects feeding applications within the
 * simulation.
 * 
 * @author Michael Tighe
 *
 */
public final class DataCentreSimulation extends Simulation {

	private ArrayList<DataCentre> datacentres = new ArrayList<DataCentre>(); //collection of datacentres within the simulation
	private Set<Workload> workloads = new HashSet<Workload>(); //set of all Workload objects feeding applications in the simulation
	VmExecutionDirector vmExecutionDirector = new VmExecutionDirector(); //handles running of VMs

	/**
	 * Create a new DataCentreSimulation with the specified name
	 * @param name
	 */
	public DataCentreSimulation(String name) {
		super(name);
	}
	
	/**
	 * Create a new DataCentreSimulation with the specified name and using the specified
	 * seed to generate random numbers.
	 * 
	 * @param name
	 * @param randomSeed
	 */
	public DataCentreSimulation(String name, long randomSeed) {
		super(name, randomSeed);
	}
	
	/**
	 * Add a DataCentre to the simulation
	 * @param dc
	 */
	public void addDatacentre(DataCentre dc) {
		datacentres.add(dc);
	}
	
	/**
	 * Add a Workload to the simulation. Note that all Workloads feeding applications
	 * within the simulation MUST be added to DataCentreSimulation
	 * @param workload
	 */
	public void addWorkload(Workload workload) {
		workloads.add(workload);
	}
	
	/**
	 * Remove a Workload from the simulation.
	 * @param workload
	 */
	public void removeWorkload(Workload workload) {
		workloads.remove(workload);
	}
	
	/**
	 * Get a list of all of the Hosts within the simulation
	 * @return
	 */
	private ArrayList<Host> getHostList() {
		
		int nHosts = 0;
		for (DataCentre dc : datacentres)
			nHosts += dc.getHosts().size();
		
		ArrayList<Host> hosts = new ArrayList<Host>(nHosts);
		
		for (DataCentre dc : datacentres) {
			hosts.addAll(dc.getHosts());
		}
		
		return hosts;
	}
	
	@Override
	public void beginSimulation() {
		logger.info("Starting DCSim");
		
		logger.info("Random Seed: " + this.getRandomSeed());
	}

	@Override
	public void updateSimulation(long simulationTime) {
		
		//retrieve work for the elapsed period since the last update
		for (Workload workload : workloads)
			workload.update();
		
		//schedule VM execution
		vmExecutionDirector.execute(getHostList());
		
		//update metrics and log info
		for (DataCentre dc : datacentres) {
			if (this.isRecordingMetrics())
				dc.updateMetrics();
			dc.logInfo();
		}
		
		if (this.isRecordingMetrics()) {	
			//update metrics tracked by workloads (i.e. SLA)
			for (Workload workload : workloads)
				workload.updateMetrics();
		}
		
				
	}

	@Override
	public void completeSimulation(long duration) {
		logger.info("DCSim Simulation Complete");
		
		//log simulation time
		double simTime = this.getDuration();
		double recordedTime = this.getRecordingDuration();
		String simUnits = "ms";
		if (simTime >= 864000000) { //>= 10 days
			simTime = simTime / 86400000;
			recordedTime = recordedTime / 86400000;
			simUnits = " days";
		} else if (simTime >= 7200000) { //>= 2 hours
			simTime = simTime / 3600000;
			recordedTime = recordedTime / 3600000;
			simUnits = "hrs";
		} else if (simTime >= 600000) { //>= 2 minutes
			simTime = simTime / 60000d;
			recordedTime = recordedTime / 60000d;
			simUnits = "mins";
		} else if (simTime >= 10000) { //>= 10 seconds
			simTime = simTime / 1000d;
			recordedTime = recordedTime / 1000d;
			simUnits = "s";
		}
		logger.info("Simulation Time: " + simTime + simUnits);
		logger.info("Recorded Time: " + recordedTime + simUnits);
	
	}
	
	public ArrayList<DataCentre> getDataCentres(){
		return datacentres;
	}

}
