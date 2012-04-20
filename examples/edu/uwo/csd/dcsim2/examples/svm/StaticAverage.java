package edu.uwo.csd.dcsim2.examples.svm;

import java.util.ArrayList;

import org.apache.log4j.*;

import edu.uwo.csd.dcsim2.*;
import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.management.*;
import edu.uwo.csd.dcsim2.vm.*;

public class StaticAverage {

	private static Logger logger = Logger.getLogger(StaticAverage.class);
	
	public static void main(String args[]) {
		
		PropertyConfigurator.configure(Simulation.getConfigDirectory() +"/logger.properties"); //configure logging from file

		logger.info(StaticAverage.class.toString());
		
		//Set random seed to repeat run
		//Utility.setRandomSeed(1088501048448116498l);
		//Utility.setRandomSeed(3081198553457496232l);
		//Utility.setRandomSeed(-2485691440833440205l);
		//Utility.setRandomSeed(2074739686644571611l);
		Utility.setRandomSeed(-1519296228623429147l);
		
		DataCentre dc = SVMHelper.createDataCentre();
		
		ArrayList<VMAllocationRequest> vmList = SVMHelper.createVmList(true);
		
		SVMHelper.placeVms(vmList, dc);
				
		SVMHelper.runSimulation(864000000, 86400000);
		
	}
	
}
