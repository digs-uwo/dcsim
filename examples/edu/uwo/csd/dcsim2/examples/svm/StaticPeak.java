package edu.uwo.csd.dcsim2.examples.svm;

import java.util.ArrayList;

import org.apache.log4j.*;

import edu.uwo.csd.dcsim2.*;
import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.management.*;
import edu.uwo.csd.dcsim2.vm.*;

public class StaticPeak {

	private static Logger logger = Logger.getLogger(StaticPeak.class);
	
	public static void main(String args[]) {
		
		PropertyConfigurator.configure(Simulation.getConfigDirectory() +"/logger.properties"); //configure logging from file

		logger.info(StaticPeak.class.toString());
		
		//Set random seed to repeat run
		//Utility.setRandomSeed(6225674672952014821l);
		
		DataCentre dc = SVMHelper.createDataCentre();
		
		ArrayList<VMAllocationRequest> vmList = SVMHelper.createVmList(false);
		
		SVMHelper.placeVms(vmList, dc);
				
		SVMHelper.runSimulation(864000000, 86400000);
		
	}
	
}
