package edu.uwo.csd.dcsim2.examples.svm;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.uwo.csd.dcsim2.*;
import edu.uwo.csd.dcsim2.application.*;
import edu.uwo.csd.dcsim2.application.workload.*;
import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.host.*;
import edu.uwo.csd.dcsim2.host.model.*;
import edu.uwo.csd.dcsim2.host.resourcemanager.*;
import edu.uwo.csd.dcsim2.host.scheduler.*;
import edu.uwo.csd.dcsim2.management.*;
import edu.uwo.csd.dcsim2.vm.*;

public class Replication {

	private static Logger logger = Logger.getLogger(Replication.class);
	
	public static void main(String args[]) {
		
		PropertyConfigurator.configure(Simulation.getConfigDirectory() +"/logger.properties"); //configure logging from file
		
		logger.info(Replication.class.toString());
		
		//Set random seed to repeat run
//		Utility.setRandomSeed(-2250887070548558400l);
//		Utility.setRandomSeed(9028535546026732111l);
//		Utility.setRandomSeed(2169085675430796896l);
//		Utility.setRandomSeed(4294062650326098795l);
//		Utility.setRandomSeed(3901350559651978320l);
		
		VMPlacementPolicy vmPlacementPolicy = new VMPlacementPolicyFFD();
		DataCentre dc = new DataCentre(vmPlacementPolicy);
		
		Simulation.getInstance().setSimulationUpdateController(new DCSimUpdateController(dc));
		
		//create hosts
		ArrayList<Host> hostList = createHosts(100);
		dc.addHosts(hostList);

		//create services and VMs
		int nService = 50;
		
		ArrayList<Service> serviceList = new ArrayList<Service>();

		for (int i = 0; i < nService; ++i) {
			int scale = (Math.abs(Utility.getRandom().nextInt()) % 4) + 2;
			serviceList.add(createService("traces/clarknet", (int)(Utility.getRandom().nextDouble() * 200000000), scale));
		}
		for (int i = 0; i < nService; ++i) {
			int scale = (Math.abs(Utility.getRandom().nextInt()) % 4) + 2;
			serviceList.add(createService("traces/epa", (int)(Utility.getRandom().nextDouble() * 40000000), scale));
		}
		for (int i = 0; i < nService; ++i) {
			int scale = (Math.abs(Utility.getRandom().nextInt()) % 4) + 2;
			serviceList.add(createService("traces/sdsc", (int)(Utility.getRandom().nextDouble() * 40000000), scale));
		}
		
//		for (int i = 0; i < 1; ++i) {
//			int scale = 5;
//			serviceList.add(createService("traces/sdsc", (int)(Utility.getRandom().nextDouble() * 40000000), scale));
//		}
		
		ArrayList<VMAllocationRequest> vmList = new ArrayList<VMAllocationRequest>();
		for (Service service : serviceList) {
			vmList.addAll(service.createInitialVmRequests());
		}
		
		vmPlacementPolicy.submitVMs(vmList);
		
		//turn off hosts with no VMs
		for (Host host : hostList) {
			if (host.getVMAllocations().size() == 0) {
				host.setState(Host.HostState.OFF);
			}
		}
		
//		@SuppressWarnings("unused")
//		ServiceReplicationPolicySimple serviceReplicationPolicy = new ServiceReplicationPolicySimple(serviceList, 600000, 0.85, 0.7, vmPlacementPolicy);
		
		
		long startTime = System.currentTimeMillis();
		logger.info("Start time: " + startTime + "ms");
			
		//run the simulation
		Simulation.getInstance().run(864000000, 86400000); //10 days
		//Simulation.getInstance().run(86400000, 0); //1 day
		//Simulation.getInstance().run(3600000, 0); //1 hour
		//Simulation.getInstance().run(600000, 0); //10 minutes 
		
		long endTime = System.currentTimeMillis();
		logger.info("End time: " + endTime + "ms. Elapsed: " + ((endTime - startTime) / 1000) + "s");
		
	}
	
	public static Service createService(String fileName, long offset, int scale) {
		
		//create workload (external)
		Workload workload = new TraceWorkload(fileName, 2700 * scale, offset); //scale to n replicas
		
		int cores = 1; //requires 1 core
		int coreCapacity = 3000;
		int memory = 1024;
		int bandwidth = 16384; //16MB = 16384KB
		long storage = 1024; //1GB
		
		SingleTierWebService webService = new SingleTierWebService(workload, cores, coreCapacity, memory, bandwidth, storage, 1, 0, 300, scale); //start with peak # of VMs
		//SingleTierWebService webService = new SingleTierWebService(workload, cores, coreCapacity, memory, bandwidth, storage, 1, 0, 300); //start with 1 VM

		return webService;

	}
	
	public static ArrayList<Host> createHosts(int nHosts) {
		
		ArrayList<Host> hosts = new ArrayList<Host>(nHosts);
		
		for (int i = 0; i < nHosts; ++i) {
			Host host = new ProLiantDL360G5E5450Host(
					new StaticOversubscribingCpuManager(500),
					new StaticMemoryManager(),
					new StaticBandwidthManager(131072), //assuming a separate 1Gb link for management!
					new StaticStorageManager(),
					new FairShareCpuScheduler());
						
			hosts.add(host);
		}
		
		return hosts;
	}
	
}
