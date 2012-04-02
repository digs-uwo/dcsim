package edu.uwo.csd.dcsim2;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim2.application.Application;
import edu.uwo.csd.dcsim2.application.workload.Workload;
import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.core.SimulationUpdateController;
import edu.uwo.csd.dcsim2.core.Utility;
import edu.uwo.csd.dcsim2.host.Host;
import edu.uwo.csd.dcsim2.host.scheduler.FairShareCpuScheduler;
import edu.uwo.csd.dcsim2.host.scheduler.MasterCpuScheduler;
import edu.uwo.csd.dcsim2.management.VMRelocationPolicy;

public class DCSimUpdateController implements SimulationUpdateController {

	private static Logger logger = Logger.getLogger(DCSimUpdateController.class);
	
	public static final int DCSIM_UPDATE_CONTROLLER_RECORD_METRICS = 1;
	
	private ArrayList<DataCentre> datacentres = new ArrayList<DataCentre>();
	
	public DCSimUpdateController(DataCentre dc) {
		addDatacentre(dc);
	}
	
	public void addDatacentre(DataCentre dc) {
		datacentres.add(dc);
	}
	
	@Override
	public void beginSimulation() {
		logger.info("Starting DCSim2");
	}
	
	@Override
	public void updateSimulation(long simulationTime) {
		//update workloads
		Workload.updateAllWorkloads();
		
		//schedule cpu
		MasterCpuScheduler.getMasterCpuScheduler().scheduleCpu();
		
		for (DataCentre dc : datacentres) {
			if (Simulation.getInstance().isRecordingMetrics())
				dc.updateMetrics();
			dc.logInfo();
		}
		
		if (Simulation.getInstance().isRecordingMetrics())
			Host.updateGlobalMetrics();
		
		//finalize workloads (print logs, calculate stats)
		//Workload.logAllWorkloads();
	}

	@Override
	public void completeSimulation(long simulationTime) {
		logger.info("DCSim2 Simulation Complete");
		
		logger.info("Total Power [" + (Host.getGlobalPowerConsumed() / 3600000d) + "kWh]");
		logger.info("Average CPU Utilization [" + (Host.getGlobalAverageUtilization() * 100) + "]");
		logger.info("Host-Hours [" + (Host.getGlobalTimeActive() / 3600000d) + "]");
		logger.info("Average Hosts [" + ((double)Host.getGlobalTimeActive() / (double)Simulation.getInstance().getRecordingDuration()) + "]");
		logger.info("Min Hosts [" + Host.getMinActiveHosts() + "]");
		logger.info("Max Hosts [" + Host.getMaxActiveHosts() + "]");
		
		double underProvision;
		underProvision = (Application.getGlobalResourceDemand().getCpu() - Application.getGlobalResourceUsed().getCpu()) / Application.getGlobalResourceDemand().getCpu();
		logger.info("CPU Underprovision [" + Utility.roundDouble((underProvision * 100), 3) + "%]");
//		underProvision = (Application.getGlobalResourceDemand().getBandwidth() - Application.getGlobalResourceUsed().getBandwidth()) / Application.getGlobalResourceDemand().getBandwidth();
//		logger.info("BW Underprovision [" + Utility.roundDouble((underProvision * 100), 3) + "%]");
		
		logger.info("VMRelocationPolicy migrations: " + VMRelocationPolicy.getMigrationCount());
		
		//logger.info("Total Work [" + Utility.roundDouble(Workload.getGlobalCompletedWork(), 3) + "/" + Utility.roundDouble(Workload.getGlobalTotalWork(), 3) + "]"); //WARNING: this metric is only meaningful if each incoming work unit is identical!
		logger.info("Total Work Missed [" + Utility.roundDouble((1 - (Workload.getGlobalCompletedWork() / Workload.getGlobalTotalWork())) * 100, 3) + "%]"); //WARNING: this metric is only meaningful if each incoming work unit is identical!
	}

}
