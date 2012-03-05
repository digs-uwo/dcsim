package edu.uwo.csd.dcsim2;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.uwo.csd.dcsim2.application.*;
import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.host.*;
import edu.uwo.csd.dcsim2.host.resourcemanager.*;
import edu.uwo.csd.dcsim2.host.scheduler.*;
import edu.uwo.csd.dcsim2.vm.*;
import edu.uwo.csd.dcsim2.management.*;

public class DCSim2 implements SimulationUpdateController {

	private static Logger logger = Logger.getLogger(DCSim2.class);
	
	private ArrayList<DataCentre> datacentres;
	
	public DCSim2() {
		/*
		 * Set up logging
		 */
		PropertyConfigurator.configure(Simulation.getConfigDirectory() +"/logger.properties"); //configure logging from file
		logger.info("Starting DCSim2");
		
		
		Simulation.getSimulation().setSimulationUpdateController(this);
		
		datacentres = new ArrayList<DataCentre>();
	}

	public void addDatacentre(DataCentre dc) {
		datacentres.add(dc);
	}
	
	public void runSimulation(long duration) {
		Simulation.getSimulation().run(duration);
	}

	@Override
	public void updateSimulation(long simulationTime) {
		//update workloads
		Workload.updateAllWorkloads();
		
		//schedule cpu
		MasterCpuScheduler.getMasterCpuScheduler().scheduleCpu();
		
		//finalize workloads (print logs, calculate stats)
		Workload.logAllWorkloads();
	}
	
	public static void main(String args[]) {
		DCSim2 simulator = new DCSim2();
		
		//create datacentre
		VMPlacementPolicy vmPlacementPolicy = new VMPlacementPolicyFFD();
		DataCentre dc = new DataCentre(vmPlacementPolicy);
		
		//create hosts
		dc.addHosts(createHosts(2));
		
		simulator.addDatacentre(dc); //TODO why? is this necessary?
		
		ArrayList<VMAllocationRequest> vmList = new ArrayList<VMAllocationRequest>();
		for (int i = 0; i < 4; ++i) {
			vmList.add(new VMAllocationRequest(createVMDesc(500, 400, 5000)));
		}
		//vmList.add(new VMAllocationRequest(createVMDesc(200)));
		
		dc.getVMPlacementPolicy().submitVMs(vmList);
		
		EventSink eventSink = simulator.new EventSink();
		for (int i = 1; i < 10; ++i) {
			Simulation.getSimulation().sendEvent(
					new Event(0, i * 1, eventSink, eventSink));
			
		}
		
		simulator.runSimulation(10000);
		
	}
	
	public static VMDescription createVMDesc(double firstWorkLevel, double secondWorkLevel, long switchTime) {
		
		//Build service
		
		//create workload (external)
		Workload workload = new TwoLevelWorkload(firstWorkLevel, secondWorkLevel, switchTime);
		
		//create single tier (web tier)
		WebServerTier webServerTier = new WebServerTier(1024, 1024); //1GB RAM, 1GB Storage, static
		webServerTier.setWorkTarget(workload);
		
		//add a load balancer to the tier, if necessary
		//webServerTier.setLoadBalancer(new EqualShareLoadBalancer());
		
		//set the tier as the target for the external workload
		workload.setWorkTarget(webServerTier);
		
		//build VMDescription
		int cores = 1; //requires 1 core
		int coreCapacity = 500; //1000 cpu shares
		int memory = 4096; //8192; //8GB
		int bandwidth = 16384; //16MB = 16384KB
		long storage = 102400; //100GB
		VMDescription vmDescription = new VMDescription(cores, coreCapacity, memory, bandwidth, storage, webServerTier);

		return vmDescription;
	}
	
	public static ArrayList<Host> createHosts(int nHosts) {
		
		int cpus = 1;
		int cores = 2;
		int coreCapacity = 1000;
		int memory = 16384; //16384MB = 16GB
		int bandwidth = 131072; //131072KB = 1Gb (gigabit)
		int storage = 1048576; //1048576MB = 1TB
		
		ArrayList<Host> hosts = new ArrayList<Host>(nHosts);
		
		for (int i = 0; i < nHosts; ++i) {
			Host host = new Host(cpus, cores, coreCapacity, memory, bandwidth, storage,
					new StaticCpuManager(),
					new StaticMemoryManager(),
					new StaticBandwidthManager(),
					new StaticStorageManager(),
					new FairShareCpuScheduler());
			hosts.add(host);
		}
		
		return hosts;
	}
	
	public class EventSink extends SimulationEntity {

		@Override
		public void handleEvent(Event e) {
			logger.info("SINK");
		}
		
	}
	
}
