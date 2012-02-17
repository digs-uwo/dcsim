package edu.uwo.csd.dcsim2;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.uwo.csd.dcsim2.application.StaticWorkload;
import edu.uwo.csd.dcsim2.application.WebServerTier;
import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.host.*;
import edu.uwo.csd.dcsim2.host.resourcemanager.*;
import edu.uwo.csd.dcsim2.vm.*;

public class DCSim2 implements SimulationUpdateController {

	private static Logger logger = Logger.getLogger(DCSim2.class);
	
	private ArrayList<DataCentre> datacentres;
	
	public DCSim2() {
		PropertyConfigurator.configure(Simulation.getConfigDirectory() +"/logger.properties"); //configure logging from file
		logger.info("Starting DCSim2");
		
		Simulation.getSimulation().setSimulationUpdateController(this);
		
		datacentres = new ArrayList<DataCentre>();
	}

	public void addDatacentre(DataCentre dc) {
		datacentres.add(dc);
	}
	
	public void runSimulation(long duration) {
		Simulation.getSimulation().setDuration(duration);
		Simulation.getSimulation().run();
	}

	@Override
	public void updateSimulation(long simulationTime) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String args[]) {
		DCSim2 simulator = new DCSim2();
		
		//create datacentre
		DataCentre dc = new DataCentre();
		
		//create hosts
		dc.addHosts(createHosts(1));
		
		simulator.addDatacentre(dc);
	}
	
	public static VMDescription createVMDesc() {
		
		//Build service
		
		//create workload (external)
		StaticWorkload workload = new StaticWorkload(100);
		
		//create single tier (web tier)
		WebServerTier webServerTier = new WebServerTier(workload);
		
		//add a load balancer to the tier, if necessary
		//webServerTier.setLoadBalancer(new EqualShareLoadBalancer());
		
		//set the tier as the target for the external workload
		workload.setWorkTarget(webServerTier);
		
		//build VMDescription
		int vCores = 1; //requires 1 core
		int vCoreCapacity = 1000; //1000 cpu shares
		int memory = 8192; //8GB
		int bandwidth = 16; //16MB
		long storage = 102400; //100GB
		VMDescription vmDescription = new VMDescription(vCores, vCoreCapacity, memory, bandwidth, storage, webServerTier);

		return vmDescription;
	}
	
	public static ArrayList<Host> createHosts(int nHosts) {
		
		int cpus = 1;
		int cores = 2;
		int coreCapacity = 1000;
		int memory = 16384; //16384MB = 16GB
		int bandwidth = 128; //128MB = 1Gb (gigabit)
		int storage = 1048576; //1048576MB = 1TB
		
		ArrayList<Host> hosts = new ArrayList<Host>(nHosts);
		
		for (int i = 0; i < nHosts; ++i) {
			Host host = new Host(cpus, cores, coreCapacity, memory, bandwidth, storage,
					new StaticCpuManager(),
					new StaticMemoryManager(),
					new StaticBandwidthManager(),
					new StaticStorageManager(),
					null);
			hosts.add(host);
		}
		
		return hosts;
	}
	
}
