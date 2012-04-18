package edu.uwo.csd.dcsim2.examples;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.uwo.csd.dcsim2.*;
import edu.uwo.csd.dcsim2.application.*;
import edu.uwo.csd.dcsim2.application.loadbalancer.*;
import edu.uwo.csd.dcsim2.application.workload.*;
import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.examples.BasicReplication.Replicator;
import edu.uwo.csd.dcsim2.host.*;
import edu.uwo.csd.dcsim2.host.model.*;
import edu.uwo.csd.dcsim2.host.resourcemanager.*;
import edu.uwo.csd.dcsim2.host.scheduler.*;
import edu.uwo.csd.dcsim2.management.*;
import edu.uwo.csd.dcsim2.vm.*;

public class ServiceReplication {

	private static Logger logger = Logger.getLogger(ServiceReplication.class);
	
	public static void main(String args[]) {
		
		PropertyConfigurator.configure(Simulation.getConfigDirectory() +"/logger.properties"); //configure logging from file
		
		logger.info(ServiceReplication.class.toString());
		
		//Set random seed to repeat run
		//Utility.setRandomSeed(-5468321996339219281l);
		
		//VMPlacementPolicy vmPlacementPolicy = new VMPlacementPolicyFixedCount(7);
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
			serviceList.add(createService("traces/clarknet", (int)(Utility.getRandom().nextDouble() * 200000000), (Math.abs(Utility.getRandom().nextInt()) % 4) + 1));
		}
		for (int i = 0; i < nService; ++i) {
			serviceList.add(createService("traces/epa", (int)(Utility.getRandom().nextDouble() * 40000000), (Math.abs(Utility.getRandom().nextInt()) % 4) + 1));
		}
		
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
		
		//create the VM relocation policy
//		@SuppressWarnings("unused")
//		VMRelocationPolicy vmRelocationPolicy = new VMRelocationPolicyST03(dc, 600000, 600000, 0.5, 0.85, 0.85);
//		//VMConsolidationPolicy vmConsolidationPolicy = new VMConsolidationPolicySimple(dc, 600000, 600001, 0.5, 0.85); //every 10 minutes
//		@SuppressWarnings("unused")
//		VMConsolidationPolicy vmConsolidationPolicy = new VMConsolidationPolicySimple(dc, 8640000, 8640001, 0.5, 0.85); //every 2.4 hours
//		//VMConsolidationPolicy vmConsolidationPolicy = new VMConsolidationPolicySimple(dc, 86400000, 86400001, 0.5, 0.85); //every day
		
		VMPlacementPolicy replicationPlacementPolicy = new VMPlacementPolicyFFD();
		replicationPlacementPolicy.setDataCentre(dc);
		
		@SuppressWarnings("unused")
		ServiceReplicationPolicySimple serviceReplicationPolicy = new ServiceReplicationPolicySimple(serviceList, 300000, 0.85, 0.6, replicationPlacementPolicy);
		
		
		long startTime = System.currentTimeMillis();
		logger.info("Start time: " + startTime + "ms");
			
		//run the simulation
		//Simulation.getInstance().run(864000000, 0); //10 days
		Simulation.getInstance().run(86400000, 0); //1 day
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
		
		SingleTierWebService webService = new SingleTierWebService(workload, cores, coreCapacity, memory, bandwidth, storage, 1, 0, 300);

		return webService;

	}
	
	public static ArrayList<Host> createHosts(int nHosts) {
		
		ArrayList<Host> hosts = new ArrayList<Host>(nHosts);
		
		for (int i = 0; i < nHosts; ++i) {
			Host host = new ProLiantDL380G5QuadCoreHost(
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
