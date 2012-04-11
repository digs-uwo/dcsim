package edu.uwo.csd.dcsim2.examples;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.uwo.csd.dcsim2.DCSimUpdateController;
import edu.uwo.csd.dcsim2.DataCentre;
import edu.uwo.csd.dcsim2.application.WebServerTier;
import edu.uwo.csd.dcsim2.application.workload.RandomWorkload;
import edu.uwo.csd.dcsim2.application.workload.Workload;
import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.core.Utility;
import edu.uwo.csd.dcsim2.host.Host;
import edu.uwo.csd.dcsim2.host.model.*;
import edu.uwo.csd.dcsim2.management.*;
import edu.uwo.csd.dcsim2.vm.VMAllocationRequest;
import edu.uwo.csd.dcsim2.vm.VMDescription;

public class CloudSimMM {
	
	private static Logger logger = Logger.getLogger(CloudSimMM.class);
	
	private static int nHosts = 100;
	
	public static void main(String args[]) {
		
		PropertyConfigurator.configure(Simulation.getConfigDirectory() +"/logger.properties"); //configure logging from file

		logger.info(CloudSimMM.class.toString());
		
		//Set random seed to repeat run
		//Utility.setRandomSeed(-5937747979384659521l);
		
		//create datacentre
		VMPlacementPolicy vmPlacementPolicy = new VMPlacementPolicyFFD(); //new VMPlacementPolicyFixedCount(7);
		//VMPlacementPolicy vmPlacementPolicy = new VMPlacementPolicyFixedCount(7);
		DataCentre dc = new DataCentre(vmPlacementPolicy);
		
		Simulation.getInstance().setSimulationUpdateController(new DCSimUpdateController(dc));
		
		//create hosts
		ArrayList<Host> hostList = createHosts(nHosts);
		dc.addHosts(hostList);
		
		//create VMs
		int nVM = 290;
		
		ArrayList<VMAllocationRequest> vmList = new ArrayList<VMAllocationRequest>();
		int vmSize[] = {250, 500, 750, 1000};
		for (int i = 0; i < nVM; ++i) {
			int vmType = i / (int) Math.ceil((double) nVM / 4);
			
			vmList.add(new VMAllocationRequest(createVMDesc(vmSize[vmType])));
		}
		Collections.shuffle(vmList, Utility.getRandom()); //should this be done?
		
		//submit VMs
		if (!dc.getVMPlacementPolicy().submitVMs(vmList))
			throw new RuntimeException("Could not place all VMs");
		
		//since this is a static run, turn off hosts with no VMs
		for (Host host : hostList) {
			if (host.getVMAllocations().size() == 0) {
				host.setState(Host.HostState.OFF);
			}
		}
		
		//create the VM relocation policy
		VMRelocationPolicy vmRelocationPolicy = new VMRelocationPolicyST03(dc, 300000, 300000, 0.4, 0.80, 0.80);
		VMConsolidationPolicy vmConsolidationPolicy = new VMConsolidationPolicySimple(dc, 300000, 300001, 0.4, 0.80); //every 10 minutes
		
		long startTime = System.currentTimeMillis();
		logger.info("Start time: " + startTime + "ms");
		
		//run the simulation
		Simulation.getInstance().run(36000000, 0); //10 hours
		//Simulation.getInstance().run(600000, 0); //10 minutes
		
		long endTime = System.currentTimeMillis();
		logger.info("End time: " + endTime + "ms. Elapsed: " + ((endTime - startTime) / 1000) + "s");
		
	}
	
	public static ArrayList<Host> createHosts(int nHosts) {
		
		ArrayList<Host> hosts = new ArrayList<Host>(nHosts);
		
		int hostSize[] = {1000, 2000, 3000};
		for (int i = 0; i < nHosts; ++i) {
			
			int hostType = i % 3;
			
			Host host = new CloudSimHost(hostSize[hostType]);
			
			hosts.add(host);
		}
		
		return hosts;
	}
	
	public static VMDescription createVMDesc(int coreCapacity) {
		
		//create workload (random)
		Workload workload = new RandomWorkload(coreCapacity, 300000);
		
		//create single tier (web tier)
		WebServerTier webServerTier = new WebServerTier(128, 1024, 1, 0, 0); //128MB RAM, 1GB Storage, 1 cpu per request, 0 bw per request, 0 cpu overhead
		webServerTier.setWorkTarget(workload);
		
		//set the tier as the target for the external workload
		workload.setWorkTarget(webServerTier);
		
		//build VMDescription
		int cores = 1; //requires 1 core
		int memory = 1024;
		int bandwidth = 0; //16MB = 16384KB
		long storage = 1024; //1GB
		VMDescription vmDescription = new VMDescription(cores, coreCapacity, memory, bandwidth, storage, webServerTier);

		return vmDescription;
	}
	
}
