package edu.uwo.csd.dcsim2;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.uwo.csd.dcsim2.core.*;

public class DCSim2 {

	private static Logger logger = Logger.getLogger(DCSim2.class);
	
	public static void main(String args[]) {
		/*
		 * Configure logging. Logging config loaded from logger.properties file.
		 * TODO: Move to Simulation?
		 */
		PropertyConfigurator.configure(Simulation.getConfigDirectory() +"/logger.properties"); //configure logging from file
		
		logger.info("Starting DCSim2");

	}
	


	
}
