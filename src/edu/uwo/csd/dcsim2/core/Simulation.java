package edu.uwo.csd.dcsim2.core;

import org.apache.log4j.Logger;

import java.util.Properties;
import java.io.*;
import java.util.PriorityQueue;
import java.util.Vector;

public class Simulation {
	
	private static Logger logger = Logger.getLogger(Simulation.class);
	
	private static String homeDirectory = null;
	private static String LOG_DIRECTORY = "/log";
	private static String CONFIG_DIRECTORY = "/config";
	
	private Properties properties;
	
	private static Simulation simulation = new Simulation(); //initialize singleton
	
	private PriorityQueue<Event> eventQueue;
	private long simulationTime; //in milliseconds
	private Vector<SimulationEntity> simulationEntities;
	private long duration;
	private SimulationUpdateController simulationUpdateController;
	private long eventSendCount = 0;
	
	public static Simulation getSimulation() {
		return simulation;
	}
	
	private Simulation() {
		eventQueue = new PriorityQueue<Event>(1000, new EventComparator());
		simulationTime = 0;
		simulationEntities = new Vector<SimulationEntity>();
		simulationUpdateController = null;
		
		/*
		 * Load configuration properties from file
		 */
		properties = new Properties();
		
		try {
			properties.load(new FileInputStream(Simulation.getConfigDirectory() + "/simulation.properties"));
		} catch (FileNotFoundException e) {
			logger.error("Properties file could not be loaded", e);
		} catch (IOException e) {
			logger.error("Properties file could not be loaded", e);
		}
		
		
	}
	
	public void run() {
		Event e;
		
		while (!eventQueue.isEmpty() && simulationTime <= duration) {
			e = eventQueue.poll();
			if (e.getTime() >= simulationTime) {
				
				//check if simulationTime is advancing
				if (simulationTime != e.getTime()) {
					simulationTime = e.getTime();
					
					if (simulationUpdateController != null)
						simulationUpdateController.updateSimulation(simulationTime);
					
					//update simulation entities
					for (SimulationEntity entity : simulationEntities) {
						if (entity instanceof UpdatingSimulationEntity) {
							((UpdatingSimulationEntity)entity).updateEntity();
						}
					}
				}
				
				e.getTarget().handleEvent(e);
			} else {
				//TODO: this is an error state, should report
			}
		}
	}
	
	public void sendEvent(Event event) {
		event.setSendOrder(++eventSendCount);
		eventQueue.add(event);
	}
	
	public long getSimulationTime() {
		return simulationTime;
	}
	
	public long getDuration() {
		return duration;
	}
	
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	public void setSimulationUpdateController(SimulationUpdateController simulationUpdateController) {
		this.simulationUpdateController = simulationUpdateController;
	}
	
	/**
	 * Preset the size of the entity list. If the number of SimulationEntities to be created is known a priori,
	 * preseting the size (before creating any SimulationEntity objects) will optimize registerEntity() performance.
	 * @param count
	 */
	public void presetEntityCount(int count) {
		simulationEntities = new Vector<SimulationEntity>(count);
	}
	
	public void registerEntity(SimulationEntity entity) {
		simulationEntities.add(entity);
	}
	
	
	/**
	 * Helper functions
	 */
	
	/**
	 * Get the directory of the manager application
	 * @return The directory of the manager application
	 */
	public static String getHomeDirectory() {
		if (homeDirectory == null) {
			File dir = new File(".");
			try {
				homeDirectory = dir.getCanonicalPath();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		return homeDirectory;
	}
	
	/**
	 * Get the directory that contains log files
	 * @return The directory that contains log files.
	 */
	public static String getLogDirectory() {
		return getHomeDirectory() + LOG_DIRECTORY;
	}
	
	/**
	 * Get the directory that contains configuration files
	 * @return The directory that contains configuration files.
	 */
	public static String getConfigDirectory() {
		return getHomeDirectory() + CONFIG_DIRECTORY;
	}
	
	/**
	 * Retrieve an application property from the configuration file or command line options. If a
	 * property is specified in both, then the command line overrides the properties file.
	 * @param name Name of property.
	 * @return The value of the property.
	 */
	public String getProperty(String name) {
		if (System.getProperty(name) != null) {
			return System.getProperty(name);
		} else {
			return properties.getProperty(name);
		}
	}
}
