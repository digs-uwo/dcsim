package edu.uwo.csd.dcsim2.examples;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.uwo.csd.dcsim2.DCSimUpdateController;
import edu.uwo.csd.dcsim2.DataCentre;
import edu.uwo.csd.dcsim2.application.WebServerTier;
import edu.uwo.csd.dcsim2.application.workload.RandomWorkload;
import edu.uwo.csd.dcsim2.application.workload.TraceWorkload;
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
	
	private static int nHosts = 1500;
	
	public static void main(String args[]) {
		
		PropertyConfigurator.configure(Simulation.getConfigDirectory() +"/logger.properties"); //configure logging from file

		logger.info(CloudSimMM.class.toString());
		
		//Set random seed to repeat run
		//Utility.setRandomSeed(8314174893186720729l);
		
		//create datacentre
		//VMPlacementPolicy vmPlacementPolicy = new VMPlacementPolicyFFD(); //new VMPlacementPolicyFixedCount(7);
		VMPlacementPolicy vmPlacementPolicy = new VMPlacementPolicyMBFD();
		DataCentre dc = new DataCentre(vmPlacementPolicy);
		
		Simulation.getInstance().setSimulationUpdateController(new DCSimUpdateController(dc));
		
		//create hosts
		ArrayList<Host> hostList = CloudSimHelper.createHosts(nHosts);
		dc.addHosts(hostList);
		
		//create VMs
//		int nVM = 290;
//		
//		ArrayList<VMAllocationRequest> vmList = new ArrayList<VMAllocationRequest>();
//		int vmSize[] = {250, 500, 750, 1000};
//		for (int i = 0; i < nVM; ++i) {
//			int vmType = i / (int) Math.ceil((double) nVM / 4);
//			
//			vmList.add(new VMAllocationRequest(CloudSimExampleHelper.createVMDesc(vmSize[vmType])));
//		}
//		Collections.shuffle(vmList, Utility.getRandom()); //should this be done?
		
		//create VMs
//		int nVM = 25;
//		
//		ArrayList<VMAllocationRequest> vmList = new ArrayList<VMAllocationRequest>();
//		for (int i = 0; i < nVM; ++i) {
//			vmList.add(new VMAllocationRequest(CloudSimExampleHelper.createTraceVMDesc("traces/clarknet", 250, (int)(Utility.getRandom().nextDouble() * 200000000))));
//		}
//		for (int i = 0; i < nVM; ++i) {
//			vmList.add(new VMAllocationRequest(CloudSimExampleHelper.createTraceVMDesc("traces/epa", 1000, (int)(Utility.getRandom().nextDouble() * 40000000))));
//		}
//		for (int i = 0; i < nVM; ++i) {
//			vmList.add(new VMAllocationRequest(CloudSimExampleHelper.createTraceVMDesc("traces/google_cores_job_type_0", 750, (int)(Utility.getRandom().nextDouble() * 15000000))));
//		}
//		for (int i = 0; i < nVM; ++i) {
//			vmList.add(new VMAllocationRequest(CloudSimExampleHelper.createTraceVMDesc("traces/google_cores_job_type_1", 500, (int)(Utility.getRandom().nextDouble() * 15000000))));
//		}
//		Collections.shuffle(vmList, Utility.getRandom());
		
		ArrayList<VMAllocationRequest> vmList = CloudSimHelper.createPlanetLabVMs("traces/planetlab/20110303", 500);
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
//		@SuppressWarnings("unused")
//		VMRelocationPolicy vmRelocationPolicy = new VMRelocationPolicyST03(dc, 300000, 300000, 0.4, 0.80, 0.80);
//		
//		@SuppressWarnings("unused")
//		VMConsolidationPolicy vmConsolidationPolicy = new VMConsolidationPolicySimple(dc, 300000, 300001, 0.4, 0.80); //every 10 minutes
		
		@SuppressWarnings("unused")
		VMAllocationPolicyMM vmAllocationPolicyMM = new VMAllocationPolicyMM(dc, 60000, 60000, 0.45, 0.81);
		
		long startTime = System.currentTimeMillis();
		logger.info("Start time: " + startTime + "ms");
		
		//run the simulation
		Simulation.getInstance().run(36000000, 0); //10 hours
		//Simulation.getInstance().run(600000, 0); //10 minutes
		
		long endTime = System.currentTimeMillis();
		logger.info("End time: " + endTime + "ms. Elapsed: " + ((endTime - startTime) / 1000) + "s");
		
	}
	
	
}
