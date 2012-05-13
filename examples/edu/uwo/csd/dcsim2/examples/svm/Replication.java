package edu.uwo.csd.dcsim2.examples.svm;

import java.util.ArrayList;
import java.util.Collection;

import edu.uwo.csd.dcsim2.*;
import edu.uwo.csd.dcsim2.application.*;
import edu.uwo.csd.dcsim2.application.workload.*;
import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.core.metrics.Metric;
import edu.uwo.csd.dcsim2.host.*;
import edu.uwo.csd.dcsim2.host.model.*;
import edu.uwo.csd.dcsim2.host.resourcemanager.*;
import edu.uwo.csd.dcsim2.host.scheduler.*;
import edu.uwo.csd.dcsim2.management.*;
import edu.uwo.csd.dcsim2.vm.*;

public class Replication {
	
	public static void main(String args[]) {
		
		Simulation.initializeLogging();
		
		runSimulation("replication-1", -2250887070548558400l);
//		runSimulation("replication-2", 9028535546026732111l);
//		runSimulation("replication-3", 2169085675430796896l);
//		runSimulation("replication-4", 4294062650326098795l);
//		runSimulation("replication-5", 3901350559651978320l);	
		
	}
	
	public static void runSimulation(String name, long seed) {
		DataCentreSimulation simulation = new DataCentreSimulation(name, seed);
		
		VMPlacementPolicy vmPlacementPolicy = new VMPlacementPolicyFFD(simulation);
		DataCentre dc = new DataCentre(vmPlacementPolicy);
		
		simulation.addDatacentre(dc);
		
		//create hosts
		ArrayList<Host> hostList = createHosts(simulation, 100);
		dc.addHosts(hostList);

		//create services and VMs
		int nService = 50;
		
		ArrayList<Service> serviceList = new ArrayList<Service>();

		for (int i = 0; i < nService; ++i) {
			int scale = (Math.abs(simulation.getRandom().nextInt()) % 4) + 2;
			serviceList.add(createService(simulation, "traces/clarknet", (int)(simulation.getRandom().nextDouble() * 200000000), scale));
		}
		for (int i = 0; i < nService; ++i) {
			int scale = (Math.abs(simulation.getRandom().nextInt()) % 4) + 2;
			serviceList.add(createService(simulation, "traces/epa", (int)(simulation.getRandom().nextDouble() * 40000000), scale));
		}
		for (int i = 0; i < nService; ++i) {
			int scale = (Math.abs(simulation.getRandom().nextInt()) % 4) + 2;
			serviceList.add(createService(simulation, "traces/sdsc", (int)(simulation.getRandom().nextDouble() * 40000000), scale));
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
		
		@SuppressWarnings("unused")
		ServiceReplicationPolicySimple serviceReplicationPolicy = new ServiceReplicationPolicySimple(simulation, serviceList, 600000, 0.85, 0.7, vmPlacementPolicy);
		
		
		//run the simulation
		Collection<Metric> metrics = simulation.run(864000000, 86400000); //10 days
		SVMHelper.printMetrics(metrics);
	}
	
	public static Service createService(DataCentreSimulation simulation, String fileName, long offset, int scale) {
		
		//create workload (external)
		Workload workload = new TraceWorkload(simulation, fileName, 2700 * scale, offset); //scale to n replicas
		simulation.addWorkload(workload);
		
		int cores = 1; //requires 1 core
		int coreCapacity = 3000;
		int memory = 1024;
		int bandwidth = 16384; //16MB = 16384KB
		long storage = 1024; //1GB
		
		//SingleTierWebService webService = new SingleTierWebService(workload, cores, coreCapacity, memory, bandwidth, storage, 1, 0, 300, scale); //start with peak # of VMs
		SingleTierWebService webService = new SingleTierWebService(workload, cores, coreCapacity, memory, bandwidth, storage, 1, 0, 300); //start with 1 VM

		return webService;

	}
	
	public static ArrayList<Host> createHosts(Simulation simulation, int nHosts) {
		
		ArrayList<Host> hosts = new ArrayList<Host>(nHosts);
		
		for (int i = 0; i < nHosts; ++i) {
			Host host = new ProLiantDL360G5E5450Host(
					simulation,
					new OversubscribingCpuManager(500),
					new SimpleMemoryManager(),
					new SimpleBandwidthManager(131072), //assuming a separate 1Gb link for management!
					new SimpleStorageManager(),
					new FairShareCpuScheduler(simulation));
						
			hosts.add(host);
		}
		
		return hosts;
	}
	
}
