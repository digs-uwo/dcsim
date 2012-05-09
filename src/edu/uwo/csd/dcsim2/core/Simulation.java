package edu.uwo.csd.dcsim2.core;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim2.core.metrics.Metric;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.io.*;
import java.util.PriorityQueue;

public abstract class Simulation implements SimulationEventListener {
	
	public static final int SIMULATION_TERMINATE_EVENT = 1;
	public static final int SIMULATION_RECORD_METRICS_EVENT = 2;
	
	
	private static Logger logger = Logger.getLogger(Simulation.class);
	
	private static String homeDirectory = null;
	private static String LOG_DIRECTORY = "/log";
	private static String CONFIG_DIRECTORY = "/config";
	
	private Properties properties;
		
	private PriorityQueue<Event> eventQueue;
	private long simulationTime; //in milliseconds
	private long lastUpdate; //in milliseconds
	private long duration;
	private long metricRecordStart;
	private boolean recordingMetrics;
	private long eventSendCount = 0;
	private Map<String, Metric> metrics = new HashMap<String, Metric>();
	
	public Simulation() {
		eventQueue = new PriorityQueue<Event>(1000, new EventComparator());
		simulationTime = 0;
		lastUpdate = 0;
		
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
	
	public final void run(long duration) {
		run(duration, 0);
	}
	
	public final void run(long duration, long metricRecordStart) {
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
		
		beginSimulation();
		
		while (!eventQueue.isEmpty() && simulationTime < duration) {
			e = eventQueue.poll();
				
			if (e.getTime() >= simulationTime) {
				
				//check if simulationTime is advancing
				if (simulationTime != e.getTime()) {
					lastUpdate = simulationTime;
					simulationTime = e.getTime();
					
					updateSimulation(simulationTime);
				}
				
				e.getTarget().handleEvent(e);
			} else {
				throw new RuntimeException("Encountered event (" + e.getType() + ") with time < current simulation time from class " + e.getSource().getClass().toString());
			}
		}
		
		completeSimulation(duration);
		outputMetrics();
	}
	
	private void outputMetrics() {
		for (Metric metric : metrics.values()) {
			logger.info(metric.getName() +
					" = " +
					metric.getValue());
		}
	}
	
	public abstract void beginSimulation();
	public abstract void updateSimulation(long simulationTime);
	public abstract void completeSimulation(long duration);
	
	public final void sendEvent(Event event) {
		event.setSendOrder(++eventSendCount);
		eventQueue.add(event);
	}
	
	@Override
	public final void handleEvent(Event e) {
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
	
	public boolean hasMetric(String name) {
		return metrics.containsKey(name);
	}
	
	public Metric getMetric(String name) {
		return metrics.get(name);
	}
	
	public void addMetric(Metric metric) {
		if (!metrics.containsKey(metric)) {
			metrics.put(metric.getName(), metric);
		} else {
			throw new RuntimeException("Metric " + metric.getName() + " already exists in simulation. Cannot add multiple copies of the same metric to the same simulation.");
		}
	}
	
	public final long getSimulationTime() {
		return simulationTime;
	}
	
	public final long getDuration() {
		return duration;
	}
	
	public final long getMetricRecordStart() {
		return metricRecordStart;
	}
	
	public final long getRecordingDuration() {
		return duration - metricRecordStart;
	}
	
	public final long getLastUpdate() {
		return lastUpdate;
	}
	
	public final long getElapsedTime() {
		return simulationTime - lastUpdate;
	}
	
	public final double getElapsedSeconds() {
		return getElapsedTime() / 1000d;
	}
	
	public final boolean isRecordingMetrics() {
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
	
	public final boolean hasProperty(String name) {
		if (System.getProperty(name) != null || properties.getProperty(name) != null)
			return true;
		return false;
	}
	
	/**
	 * Retrieve an application property from the configuration file or command line options. If a
	 * property is specified in both, then the command line overrides the properties file.
	 * @param name Name of property.
	 * @return The value of the property.
	 */
	public final String getProperty(String name) {
		String prop = null;
		if (System.getProperty(name) != null) {
			prop = System.getProperty(name);
		} else {
			prop = properties.getProperty(name);
		}
		
		if (prop == null)
			throw new RuntimeException("Simulation property '" + name + "' not found");
		
		return prop;
	}

	
}
