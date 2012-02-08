package edu.uwo.csd.dcsim2.core;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;
import java.util.ArrayList;

public class Simulation {
	
	private static String homeDirectory = null;
	private static String LOG_DIRECTORY = "/log";
	private static String CONFIG_DIRECTORY = "/config";
	
	private static Simulation simulation;
	
	private TreeSet<Event> eventQueue;
	private long simulationTime; //in milliseconds
	private ArrayList<SimulationEntity> simulationEntities;
	
	public static Simulation getSimulation() {
		if (simulation == null)
			simulation = new Simulation();
		return simulation;
	}
	
	private Simulation() {
		eventQueue = new TreeSet<Event>(new EventComparator());
		simulationTime = 0;
		simulationEntities = new ArrayList<SimulationEntity>();
	}
	
	public void run() {
		Event e;
		
		while (!eventQueue.isEmpty()) {
			e = eventQueue.pollFirst();
			if (e.getTime() >= simulationTime) {
				
				//check if simulationTime is advancing
				if (simulationTime != e.getTime()) {
					simulationTime = e.getTime();
					
					//update simulation entities
					for (SimulationEntity entity : simulationEntities) {
						entity.updateEntity();
					}
				}
				
				e.getTarget().handleEvent(e);
			} else {
				//TODO: this is an error state, should report
			}
		}
	}
	
	public long getSimulationTime() {
		return simulationTime;
	}
	
	/**
	 * Preset the size of the entity list. If the number of SimulationEntities to be created is known a priori,
	 * preseting the size (before creating any SimulationEntity objects) will optimize registerEntity() performance.
	 * @param count
	 */
	public void presetEntityCount(int count) {
		simulationEntities = new ArrayList<SimulationEntity>(count);
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
}
