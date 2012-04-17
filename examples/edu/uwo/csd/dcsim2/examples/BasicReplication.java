package edu.uwo.csd.dcsim2.examples;

import java.util.ArrayList;

import org.apache.log4j.*;

import edu.uwo.csd.dcsim2.*;
import edu.uwo.csd.dcsim2.application.*;
import edu.uwo.csd.dcsim2.application.loadbalancer.*;
import edu.uwo.csd.dcsim2.application.workload.*;
import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.host.*;
import edu.uwo.csd.dcsim2.host.model.*;
import edu.uwo.csd.dcsim2.host.power.*;
import edu.uwo.csd.dcsim2.host.resourcemanager.*;
import edu.uwo.csd.dcsim2.host.scheduler.*;
import edu.uwo.csd.dcsim2.management.*;
import edu.uwo.csd.dcsim2.vm.*;

public class BasicReplication {

	private static Logger logger = Logger.getLogger(BasicReplication.class);
	
	public static void main(String args[]) {
	
		PropertyConfigurator.configure(Simulation.getConfigDirectory() +"/logger.properties"); //configure logging from file
		
		logger.info(BasicReplication.class.toString());
		
		//Set random seed to repeat run
		//Utility.setRandomSeed(2145730755378205824l);
		
		VMPlacementPolicy vmPlacementPolicy = new VMPlacementPolicyFixedCount(7);
		DataCentre dc = new DataCentre(vmPlacementPolicy);
		
		Simulation.getInstance().setSimulationUpdateController(new DCSimUpdateController(dc));
		
		//create hosts
		ArrayList<Host> hostList = createHosts(2);
		dc.addHosts(hostList);

		VMAllocationRequest vmAllocationRequest = createServiceVmList().get(0);
		
		vmPlacementPolicy.submitVM(vmAllocationRequest, hostList.get(0));
		
		long startTime = System.currentTimeMillis();
		logger.info("Start time: " + startTime + "ms");
		
		BasicReplication br = new BasicReplication();
		Replicator replicator = br.new Replicator(hostList.get(0), hostList.get(1), vmPlacementPolicy);
		
		Simulation.getInstance().sendEvent(new Event(0, 120000, dc, replicator)); //replicate 2 minutes in
		Simulation.getInstance().sendEvent(new Event(1, 240000, dc, replicator)); //de-replicate 4 minutes in
		
		//run the simulation
		//Simulation.getInstance().run(3600000, 0); //1 hour
		Simulation.getInstance().run(600000, 0); //10 minutes 
		
		long endTime = System.currentTimeMillis();
		logger.info("End time: " + endTime + "ms. Elapsed: " + ((endTime - startTime) / 1000) + "s");
		
	}
	
	public static ArrayList<VMAllocationRequest> createServiceVmList() {
		
		//create workload (external)
		Workload workload = new StaticWorkload(2000);
		
		int cores = 1; //requires 1 core
		int coreCapacity = 3000;
		int memory = 1024;
		int bandwidth = 16384; //16MB = 16384KB
		long storage = 1024; //1GB
		
		SingleTierWebService webService = new SingleTierWebService(workload, cores, coreCapacity, memory, bandwidth, storage, 1, 0, 300);

		return webService.createInitialVmRequests();

	}
	
	public static ArrayList<Host> createHosts(int nHosts) {
		
		ArrayList<Host> hosts = new ArrayList<Host>(nHosts);
		
		for (int i = 0; i < nHosts; ++i) {
			Host host = new ProLiantDL380G5QuadCoreHost(
					new StaticOversubscribingCpuManager(1500), //300 VMM overhead + 200 migration reserve
					new StaticMemoryManager(),
					new StaticBandwidthManager(131072), //assuming a separate 1Gb link for management!
					new StaticStorageManager(),
					new FairShareCpuScheduler());
						
			hosts.add(host);
		}
		
		return hosts;
	}
	
	public class Replicator extends SimulationEntity {

		Host sourceHost;
		Host targetHost;
		VMPlacementPolicy vmPlacementPolicy;
		
		public Replicator(Host sourceHost, Host targetHost, VMPlacementPolicy vmPlacementPolicy) {
			this.sourceHost = sourceHost;
			this.targetHost = targetHost;
			this.vmPlacementPolicy = vmPlacementPolicy;
		}
		
		@Override
		public void handleEvent(Event e) {
			if (e.getType() == 0) {
				//replicate!
				System.out.println("Replicating...");
				VMAllocation vmAllocation = sourceHost.getVMAllocations().get(0);
				
				VMAllocationRequest replicaRequest = new VMAllocationRequest(vmAllocation.getVMDescription());
				
				targetHost.submitVM(replicaRequest);
			} else {
				//de-replicate!
				System.out.println("Removing replica...");
				
				VMAllocation vmAllocation = sourceHost.getVMAllocations().get(0);
				
				vmAllocation.getVm().stopApplication();
				sourceHost.deallocate(vmAllocation);
				
			}
			
		}
		
	}
	
}

