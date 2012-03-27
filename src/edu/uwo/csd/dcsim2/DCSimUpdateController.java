package edu.uwo.csd.dcsim2;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.uwo.csd.dcsim2.application.workload.Workload;
import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.core.SimulationUpdateController;
import edu.uwo.csd.dcsim2.host.scheduler.MasterCpuScheduler;

public class DCSimUpdateController implements SimulationUpdateController {

	private static Logger logger = Logger.getLogger(DCSimUpdateController.class);
	
	private ArrayList<DataCentre> datacentres = new ArrayList<DataCentre>();;
	
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
			dc.updateMetrics();
			dc.logInfo();
		}
		
		//finalize workloads (print logs, calculate stats)
		Workload.logAllWorkloads();
	}

	@Override
	public void completeSimulation(long simulationTime) {
		logger.info("DCSim2 Simulation Complete");
	}

}
