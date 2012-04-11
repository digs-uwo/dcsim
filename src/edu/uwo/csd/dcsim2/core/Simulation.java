package edu.uwo.csd.dcsim2.core;

import org.apache.log4j.Logger;

import java.util.Properties;
import java.io.*;
import java.util.PriorityQueue;

public class Simulation extends SimulationEntity {
	
	public static final int SIMULATION_TERMINATE_EVENT = 1;
	public static final int SIMULATION_RECORD_METRICS_EVENT = 2;
	
	
	private static Logger logger = Logger.getLogger(Simulation.class);
	
	private static String homeDirectory = null;
	private static String LOG_DIRECTORY = "/log";
	private static String CONFIG_DIRECTORY = "/config";
	
	private Properties properties;
	
	private static Simulation simulation = new Simulation(); //initialize singleton
	
	private PriorityQueue<Event> eventQueue;
	private long simulationTime; //in milliseconds
	private long lastUpdate; //in milliseconds
	private long duration;
	private long metricRecordStart;
	private boolean recordingMetrics;
	private long eventSendCount = 0;
	
	private SimulationUpdateController simulationUpdateController;
		
	public static Simulation getInstance() {
		return simulation;
	}
	
	private Simulation() {
		eventQueue = new PriorityQueue<Event>(1000, new EventComparator());
		simulationTime = 0;
		lastUpdate = 0;
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
	
	public void run(long duration) {
		run(duration, 0);
	}
	
	public void run(long duration, long metricRecordStart) {
		Event e;
		
		//configure simulation duration
		this.duration = duration;
		sendEvent(new Event(Simulation.SIMULATION_TERMINATE_EVENT, duration, this, this)); //this event runs at the last possible time in the simulation to ensure simulation updates
		
		if (metricRecordStart > 0) {
			recordingMetrics = false;
			this.metricRecordStart = metricRecordStart;
			sendEvent(new Event(Simulation.SIMULATION_RECORD_METRICS_EVENT, metricRecordStart, this, this));
		} else {
			recordingMetrics = true;
		}
		
		if (simulationUpdateController != null)
			simulationUpdateController.beginSimulation();
		
		while (!eventQueue.isEmpty() && simulationTime < duration) {
			e = eventQueue.poll();
				
			if (e.getTime() >= simulationTime) {
				
				//check if simulationTime is advancing
				if (simulationTime != e.getTime()) {
					lastUpdate = simulationTime;
					simulationTime = e.getTime();
					
					if (simulationUpdateController != null)
						simulationUpdateController.updateSimulation(simulationTime);
					
					//update simulation entities
					for (SimulationEntity entity : SimulationEntity.getSimulationEntities()) {
						if (entity instanceof UpdatingSimulationEntity) {
							((UpdatingSimulationEntity)entity).updateEntity();
						}
					}
				}
				
				e.getTarget().handleEvent(e);
			} else {
				throw new RuntimeException("Encountered event (" + e.getType() + ") with time < current simulation time from class " + e.getSource().getClass().toString());
			}
		}
		
		if (simulationUpdateController != null)
			simulationUpdateController.completeSimulation(duration);
	}
	
	public void sendEvent(Event event) {
		event.setSendOrder(++eventSendCount);
		eventQueue.add(event);
	}
	
	@Override
	public void handleEvent(Event e) {
		switch (e.getType()) {
			case Simulation.SIMULATION_TERMINATE_EVENT:
				//Do nothing. This ensures that the simulation is fully up-to-date upon termination.
				logger.debug("Terminating Simulation");
				break;
			case Simulation.SIMULATION_RECORD_METRICS_EVENT:
				logger.debug("Metric recording started");
				recordingMetrics = true;
				break;
			default:
				throw new RuntimeException("Simulation received unknown event type");
		}
	}
	
	public long getSimulationTime() {
		return simulationTime;
	}
	
	public long getDuration() {
		return duration;
	}
	
	public long getMetricRecordStart() {
		return metricRecordStart;
	}
	
	public long getRecordingDuration() {
		return duration - metricRecordStart;
	}
	
	public long getLastUpdate() {
		return lastUpdate;
	}
	
	public long getElapsedTime() {
		return simulationTime - lastUpdate;
	}
	
	public double getElapsedSeconds() {
		return getElapsedTime() / 1000d;
	}
	
	public void setSimulationUpdateController(SimulationUpdateController simulationUpdateController) {
		this.simulationUpdateController = simulationUpdateController;
	}
	
	public SimulationUpdateController getSimulationUpdateController() {
		return simulationUpdateController;
	}
	
	public boolean isRecordingMetrics() {
		return recordingMetrics;
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
