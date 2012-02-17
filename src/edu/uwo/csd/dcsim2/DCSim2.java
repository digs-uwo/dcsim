package edu.uwo.csd.dcsim2;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.uwo.csd.dcsim2.core.*;
import edu.uwo.csd.dcsim2.host.*;

public class DCSim2 implements SimulationUpdateController {

	private static Logger logger = Logger.getLogger(DCSim2.class);
	
	private ArrayList<Datacentre> datacentres;
	
	public DCSim2() {
		PropertyConfigurator.configure(Simulation.getConfigDirectory() +"/logger.properties"); //configure logging from file
		logger.info("Starting DCSim2");
		
		Simulation.getSimulation().setSimulationUpdateController(this);
		
		datacentres = new ArrayList<Datacentre>();
	}

	public void addDatacentre(Datacentre dc) {
		datacentres.add(dc);
	}
	
	public void runSimulation(long duration) {
		Simulation.getSimulation().setDuration(duration);
		Simulation.getSimulation().run();
	}

	@Override
	public void updateSimulation(long simulationTime) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String args[]) {
		DCSim2 simulator = new DCSim2();
		
		//create datacentre
		Datacentre dc = new Datacentre();
		
		//create hosts
		
		
		simulator.addDatacentre(dc);
	}
	
	public static Host createHost() {
		
		return null;
	}
	
}
