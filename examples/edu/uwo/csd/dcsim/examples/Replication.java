package edu.uwo.csd.dcsim.examples;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim.*;
import edu.uwo.csd.dcsim.application.*;
import edu.uwo.csd.dcsim.application.workload.*;
import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.host.*;
import edu.uwo.csd.dcsim.host.resourcemanager.*;
import edu.uwo.csd.dcsim.host.scheduler.*;
import edu.uwo.csd.dcsim.management.*;
import edu.uwo.csd.dcsim.vm.*;

public class Replication extends DCSimulationTask {
	
	private static Logger logger = Logger.getLogger(Replication.class);
	
	public static void main(String args[]) {
		
		Simulation.initializeLogging();
		
		Collection<SimulationTask> completedTasks;
		SimulationExecutor executor = new SimulationExecutor();
		
		executor.addTask(new Replication("replication-1", 1088501048448116498l));
//		executor.addTask(new Replication("replication-2", 3081198553457496232l));
//		executor.addTask(new Replication("replication-3", -2485691440833440205l));
//		executor.addTask(new Replication("replication-4", 2074739686644571611l));
//		executor.addTask(new Replication("replication-5", -1519296228623429147l));
		
		completedTasks = executor.execute();
		
		for(SimulationTask task : completedTasks) {
			logger.info(task.getName());
			ExampleHelper.printMetrics(task.getResults());
		}
		
	}
	
	public Replication(String name, long randomSeed) {
		super(name, 864000000);
		this.setMetricRecordStart(86400000);
		this.setRandomSeed(randomSeed);
	}
	
	@Override
	public void setup(DataCentreSimulation simulation) {
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
		
		ServiceReplicationPolicySimple serviceReplicationPolicy = new ServiceReplicationPolicySimple(serviceList, 0.85, 0.7, vmPlacementPolicy);
		DaemonScheduler daemon = new FixedIntervalDaemonScheduler(simulation, 600000, serviceReplicationPolicy);
		daemon.start(600000);
		
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
		
		//Create a service that always has enough VMs to serve the peak workload
		//Service service = Services.singleTierInteractiveService(workload, cores, coreCapacity, memory, bandwidth, storage, 1, 0, 300, scale, Integer.MAX_VALUE);  

		//Create a service that has a minimum of 1 VM and can scale up indefinitely
		Service service = Services.singleTierInteractiveService(workload, cores, coreCapacity, memory, bandwidth, storage, 1, 0, 300, 1, Integer.MAX_VALUE); 

		return service;

	}
	
	public static ArrayList<Host> createHosts(Simulation simulation, int nHosts) {
		
		ArrayList<Host> hosts = new ArrayList<Host>(nHosts);
		
		Host.Builder proLiantDL360G5E5450 = HostModels.ProLiantDL360G5E5450(simulation).privCpu(500).privBandwidth(131072)
				.cpuManagerFactory(new OversubscribingCpuManagerFactory())
				.memoryManagerFactory(new SimpleMemoryManagerFactory())
				.bandwidthManagerFactory(new SimpleBandwidthManagerFactory())
				.storageManagerFactory(new SimpleStorageManagerFactory())
				.cpuSchedulerFactory(new FairShareCpuSchedulerFactory(simulation));
		
		for (int i = 0; i < nHosts; ++i) {						
			hosts.add(proLiantDL360G5E5450.build());
		}
		
		return hosts;
	}


	
}
