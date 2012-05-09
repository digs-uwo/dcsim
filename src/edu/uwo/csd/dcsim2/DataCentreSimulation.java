package edu.uwo.csd.dcsim2;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim2.application.Application;
import edu.uwo.csd.dcsim2.application.workload.Workload;
import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.core.SimulationEventListener;
import edu.uwo.csd.dcsim2.core.Utility;
import edu.uwo.csd.dcsim2.host.Host;
import edu.uwo.csd.dcsim2.host.scheduler.MasterCpuScheduler;
import edu.uwo.csd.dcsim2.management.action.MigrationAction;
import edu.uwo.csd.dcsim2.management.action.ReplicateAction;
import edu.uwo.csd.dcsim2.management.action.ShutdownVmAction;

public class DataCentreSimulation extends Simulation {

	private static Logger logger = Logger.getLogger(DataCentreSimulation.class);
	
	private ArrayList<DataCentre> datacentres = new ArrayList<DataCentre>();

	public void addDatacentre(DataCentre dc) {
		datacentres.add(dc);
	}
	
	@Override
	public void beginSimulation() {
		logger.info("Starting DCSim2");
		
		logger.info("Random Seed: " + Utility.getRandomSeed());
	}

	@Override
	public void updateSimulation(long simulationTime) {
		//update workloads
		Workload.updateAllWorkloads();
		
		//schedule cpu
		MasterCpuScheduler.getMasterCpuScheduler().scheduleCpu();
		
		for (DataCentre dc : datacentres) {
			if (this.isRecordingMetrics())
				dc.updateMetrics();
			dc.logInfo();
		}
		
		if (this.isRecordingMetrics())
			Host.updateGlobalMetrics(this);
		
		//finalize workloads (print logs, calculate stats)
		//Workload.logAllWorkloads();
	}

	@Override
	public void completeSimulation(long duration) {
		logger.info("DCSim2 Simulation Complete");
		
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
