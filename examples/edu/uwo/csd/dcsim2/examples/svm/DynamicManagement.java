package edu.uwo.csd.dcsim2.examples.svm;

import java.util.ArrayList;

import org.apache.log4j.*;

import edu.uwo.csd.dcsim2.*;
import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.management.*;
import edu.uwo.csd.dcsim2.vm.*;

public class DynamicManagement {

	private static Logger logger = Logger.getLogger(DynamicManagement.class);
	
	public static void main(String args[]) {
		
		PropertyConfigurator.configure(Simulation.getConfigDirectory() +"/logger.properties"); //configure logging from file

		logger.info(DynamicManagement.class.toString());
		
		long startTime = System.currentTimeMillis();
		logger.info("Start time: " + startTime + "ms");
		
		//Set random seed to repeat run
		Utility.setRandomSeed(1088501048448116498l);
		//Utility.setRandomSeed(3081198553457496232l);
		//Utility.setRandomSeed(-2485691440833440205l);
		//Utility.setRandomSeed(2074739686644571611l);
		//Utility.setRandomSeed(-1519296228623429147l);
		
		DataCentreSimulation simulation = new DataCentreSimulation();
		
		DataCentre dc = SVMHelper.createDataCentre(simulation);
		simulation.addDatacentre(dc);
		
		ArrayList<VMAllocationRequest> vmList = SVMHelper.createVmList(simulation, false);
				
		SVMHelper.placeVms(vmList, dc);
		
		//create the VM relocation policy
		
		/*
		 * Basic Greedy Relocation & Consolidation together. Relocation same as RelocST03, Consolidation similar but
		 * evicts ALL VMs from underprovisioned hosts, not 1.
		 */
		@SuppressWarnings("unused")
		VMAllocationPolicyGreedy vmAllocationPolicyGreedy = new VMAllocationPolicyGreedy(simulation, dc, 600000, 600000, 0.5, 0.85, 0.85);

		simulation.run(864000000, 86400000);
		
		long endTime = System.currentTimeMillis();
		logger.info("End time: " + endTime + "ms. Elapsed: " + ((endTime - startTime) / 1000) + "s");
	}
	
}
