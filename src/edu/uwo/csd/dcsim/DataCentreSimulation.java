package edu.uwo.csd.dcsim;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import edu.uwo.csd.dcsim.application.workload.Workload;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.host.Host;

public final class DataCentreSimulation extends Simulation {

	private ArrayList<DataCentre> datacentres = new ArrayList<DataCentre>();
	private Set<Workload> workloads = new HashSet<Workload>();
	VmExecutionDirector vmExecutionDirector = new VmExecutionDirector();

	public DataCentreSimulation(String name) {
		super(name);
	}
	
	public DataCentreSimulation(String name, long randomSeed) {
		super(name, randomSeed);
	}
	
	public void addDatacentre(DataCentre dc) {
		datacentres.add(dc);
	}
	
	public void addWorkload(Workload workload) {
		workloads.add(workload);
	}
	
	public void removeWorkload(Workload workload) {
		workloads.remove(workload);
	}
	
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
				
		for (Workload workload : workloads)
			workload.update();
		
		//schedule cpu
		vmExecutionDirector.execute(getHostList());
		
		for (DataCentre dc : datacentres) {
			if (this.isRecordingMetrics())
				dc.updateMetrics();
			dc.logInfo();
		}
		
		if (this.isRecordingMetrics()) {
			Host.updateSimulationScopeMetrics(this);
	
			for (Workload workload : workloads)
				workload.updateMetrics();
		}
				
	}

	@Override
	public void completeSimulation(long duration) {
		logger.info("DCSim Simulation Complete");
		
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

}
