package edu.uwo.csd.dcsim2.examples;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.uwo.csd.dcsim2.*;
import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.core.metrics.Metric;
import edu.uwo.csd.dcsim2.host.Host;
import edu.uwo.csd.dcsim2.management.*;
import edu.uwo.csd.dcsim2.vm.VMAllocationRequest;

public class CloudSimMM {
	
	private static Logger logger = Logger.getLogger(CloudSimMM.class);
	
	private static int nHosts = 1500;
	
	public static void main(String args[]) {
		
		PropertyConfigurator.configure(Simulation.getConfigDirectory() +"/logger.properties"); //configure logging from file

		logger.info(CloudSimMM.class.toString());
		
		DataCentreSimulation simulation = new DataCentreSimulation();
		
		//create datacentre
		VMPlacementPolicy vmPlacementPolicy = new VMPlacementPolicyMBFD(simulation);
		DataCentre dc = new DataCentre(vmPlacementPolicy);
		
		simulation.addDatacentre(dc);
				
		//create hosts
		ArrayList<Host> hostList = CloudSimHelper.createHosts(simulation, nHosts);
		dc.addHosts(hostList);
	
		ArrayList<VMAllocationRequest> vmList = CloudSimHelper.createPlanetLabVMs(simulation, "traces/planetlab/20110303", 500);
		Collections.shuffle(vmList, simulation.getRandom()); //should this be done?
				
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
		
		//create the VM relocation policy
		@SuppressWarnings("unused")
		VMAllocationPolicyGreedy vmAllocationPolicyGreedy = new VMAllocationPolicyGreedy(simulation, dc, 600000, 600000, 0.5, 0.85, 0.85);
		
//		@SuppressWarnings("unused")
//		VMAllocationPolicyMM vmAllocationPolicyMM = new VMAllocationPolicyMM(dc, 60000, 60000, 0.45, 0.81);
				
		//run the simulation
		Collection<Metric> metrics = simulation.run(36000000, 0); //10 hours
		//Collection<Metric> metrics = simulation.run(600000, 0); //10 minutes
		
		for (Metric metric : metrics) {
			logger.info(metric.getName() + " = " + metric.toString());
		}
		
	}
	
	
}
