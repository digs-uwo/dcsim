package edu.uwo.csd.dcsim.core;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.uwo.csd.dcsim.common.Utility;
import edu.uwo.csd.dcsim.core.metrics.*;
import edu.uwo.csd.dcsim.logging.*;

import java.util.*;
import java.io.*;

public abstract class Simulation implements SimulationEventListener {
	
	public static final String DEFAULT_LOGGER_CONVERSION_PATTERN = "%-10s %-5p - %m%n";
	public static final String DEFAULT_LOGGER_DATE_FORMAT = "yyyy_MM_dd'-'HH_mm_ss";
	public static final String DEFAULT_LOGGER_FILE_NAME = "dcsim-%n-%d";
	
	public static final int SIMULATION_TERMINATE_EVENT = 1;
	public static final int SIMULATION_RECORD_METRICS_EVENT = 2;
	public static final int SIMULATION_RUN_MONITORS_EVENT = 3;
	
	private static String homeDirectory = null;
	private static String LOG_DIRECTORY = "/log";
	private static String CONFIG_DIRECTORY = "/config";
	private static String OUTPUT_DIRECTORY = "/output";
	
	private static String METRIC_PRECISION_PROP = "metricPrecision";
	
	private static Properties loggerProperties;
	
	protected final Logger logger;
	private static Properties properties;
	
	private String name;
	private PriorityQueue<Event> eventQueue;
	private long simulationTime; //in milliseconds
	private long lastUpdate; //in milliseconds
	private long duration;
	private long metricRecordStart;
	private boolean recordingMetrics;
	private long eventSendCount = 0;
	protected Map<String, Metric> metrics = new HashMap<String, Metric>();
	
	private Vector<Monitor> monitors = new Vector<Monitor>();
	
	private Random random;
	private long randomSeed;
	
	private Map<String, Integer> nextIdMap = new HashMap<String, Integer>();
	
	private boolean complete = false;
	
	public static final void initializeLogging() {
		
		Properties properties = new Properties();
		
		try {
			properties.load(new FileInputStream(Simulation.getConfigDirectory() + "/logger.properties"));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Logging properties file could not be loaded", e);
		} catch (IOException e) {
			throw new RuntimeException("Logging properties file could not be loaded", e);
		}
		
		PropertyConfigurator.configure(properties);
		loggerProperties = properties;
	}
	
	private static final Properties getProperties() {
		if (properties == null) {
			/*
			 * Load configuration properties from fileSIMULATION_RUN_MONITORS_EVENT
			 */
			properties = new Properties();
			
			try {
				properties.load(new FileInputStream(Simulation.getConfigDirectory() + "/simulation.properties"));
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Properties file could not be loaded", e);
			} catch (IOException e) {
				throw new RuntimeException("Properties file could not be loaded", e);
			}
		}
		
		return properties;
	}
	
	public Simulation(String name, long randomSeed) {
		this(name);
		this.setRandomSeed(randomSeed); //override Random seed with specified value
	}
	
	public Simulation(String name) {
		eventQueue = new PriorityQueue<Event>(1000, new EventComparator());
		simulationTime = 0;
		lastUpdate = 0;
		this.name = name;
		
		//configure simulation logger
		logger = Logger.getLogger("simLogger." + name);
		
		boolean enableFileLogging = true;
		String conversionPattern = DEFAULT_LOGGER_CONVERSION_PATTERN;
		String dateFormat = DEFAULT_LOGGER_DATE_FORMAT;
		String fileName = DEFAULT_LOGGER_FILE_NAME;
		
		if (loggerProperties != null) {
			if (loggerProperties.getProperty("log4j.logger.simLogger.enableFile") != null) {
				enableFileLogging = Boolean.parseBoolean(loggerProperties.getProperty("log4j.logger.simLogger.enableFile"));
			}
			if (loggerProperties.getProperty("log4j.logger.simLogger.ConversionPattern") != null) {
				conversionPattern = loggerProperties.getProperty("log4j.logger.simLogger.ConversionPattern");
			}
			if (loggerProperties.getProperty("log4j.logger.simLogger.DateFormat") != null) {
				dateFormat = loggerProperties.getProperty("log4j.logger.simLogger.DateFormat");
			}
			if (loggerProperties.getProperty("log4j.logger.simLogger.File") != null) {
				fileName = loggerProperties.getProperty("log4j.logger.simLogger.File");
			}
		}

		if (enableFileLogging) {
			SimulationFileAppender simAppender = new SimulationFileAppender();
			
			SimulationPatternLayout patternLayout = new SimulationPatternLayout(this);
			patternLayout.setConversionPattern(conversionPattern);
			simAppender.setLayout(patternLayout);
			simAppender.setSimName(name);
			simAppender.setDateFormat(dateFormat);
			simAppender.setFile(getLogDirectory() + "/" + fileName);
			simAppender.activateOptions();
			logger.addAppender(simAppender);
		}
		
		//initialize Random
		setRandomSeed(new Random().nextLong());
		
	}
	
	public final Collection<Metric> run(long duration) {
		return run(duration, 0);
	}
	
	public final Collection<Metric> run(long duration, long metricRecordStart) {
		
		if (complete)
			throw new IllegalStateException("Simulation has already been run");
		
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
		
		logger.info("Starting simulation " + name);
		
		beginSimulation();
		
		while (!eventQueue.isEmpty() && simulationTime < duration) {
			e = eventQueue.poll();
			
			if (e.getTime() >= simulationTime) {

				//check if simulationTime is advancing
				if (simulationTime != e.getTime()) {
					
					if (simulationTime != 0) {
						//inform metrics that this time interval update is complete
						for (Metric metric : this.metrics.values()) {
							metric.completeTimeInterval();
						}
					}
					
					lastUpdate = simulationTime;
					simulationTime = e.getTime();
					
					//inform metrics that we are starting a new time interval
					for (Metric metric : this.metrics.values())
						metric.startTimeInterval();
					
					//update the simulation
					updateSimulation(simulationTime);

					//run monitors
					if (monitors.size() > 0) {
						long nextMonitor = duration;
						for (Monitor monitor : monitors) {
							long nextExec = monitor.run();
							if (nextExec < nextMonitor)
								nextMonitor = nextExec;
						}
						sendEvent(new Event(Simulation.SIMULATION_RUN_MONITORS_EVENT, nextMonitor, this, this));
					}
					
				}
				
				e.getTarget().handleEvent(e);
			} else {
				throw new RuntimeException("Encountered event (" + e.getType() + ") with time < current simulation time from class " + e.getSource().getClass().toString());
			}
		}
		
		//inform metrics that this time interval update is complete
		for (Metric metric : this.metrics.values()) {
			metric.completeTimeInterval();
		}
		
		completeSimulation(duration);
		
		logger.info("Completed simulation " + name);
		
		complete = true;
		
		//wrap result in new Collection so that Collection is modifyable, as modifying the values() collection of a HashMap directly breaks things.
		Vector<Metric> result = new Vector<Metric>(metrics.values());
		Collections.sort(result, new MetricAlphaComparator());
		
		return result;
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
			case Simulation.SIMULATION_RUN_MONITORS_EVENT:
				//Do nothing. This ensures that monitors are run in the case that no other event is scheduled.
				break;
			default:
				throw new RuntimeException("Simulation received unknown event type");
		}
	}
	
	public final Logger getLogger() {
		return logger;
	}
	
	public final int nextId(String name) {
		int id = 1;
		
		if (nextIdMap.containsKey(name))
			id = nextIdMap.get(name);
		
		nextIdMap.put(name, id + 1);
		
		return id;
	}
	
	public final String getName() {
		return name;
	}
	
	public final Random getRandom() {
		if (random == null) {
			random = new Random();
			setRandomSeed(random.nextLong());
			//setRandomSeed()
		}
	
		return random;
	}
	
	public final long getRandomSeed() {
		return randomSeed;
	}
	
	public final void setRandomSeed(long seed) {
		randomSeed = seed;
		random = new Random(randomSeed);
	}
	
	public final boolean hasMetric(String name) {
		return metrics.containsKey(name);
	}
	
	public final Metric getMetric(String name) {
		return metrics.get(name);
	}
	
	public final void addMetric(Metric metric) {
		if (!metrics.containsKey(metric)) {
			metrics.put(metric.getName(), metric);
		} else {
			throw new RuntimeException("Metric " + metric.getName() + " already exists in simulation. Cannot add multiple copies of the same metric to the same simulation.");
		}
	}
	
	public final void addMonitor(Monitor monitor) {
		monitors.add(monitor);
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
	public static final String getHomeDirectory() {
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
	public static final String getLogDirectory() {
		return getHomeDirectory() + LOG_DIRECTORY;
	}
	
	/**
	 * Get the directory that contains configuration files
	 * @return The directory that contains configuration files.
	 */
	public static final String getConfigDirectory() {
		return getHomeDirectory() + CONFIG_DIRECTORY;
	}
	
	/**
	 * Get the directory that contains simulation trace files
	 * @return The directory that contains configuration files.
	 */
	public static final String getOutputDirectory() {
		//ensure directory exists
		File file = new File(getHomeDirectory() + OUTPUT_DIRECTORY);
		if (!file.exists())
			file.mkdir();
		
		return getHomeDirectory() + OUTPUT_DIRECTORY;
	}
	
	public static final boolean hasProperty(String name) {
		if (System.getProperty(name) != null || getProperties().getProperty(name) != null)
			return true;
		return false;
	}
	
	/**
	 * Retrieve an application property from the configuration file or command line options. If a
	 * property is specified in both, then the command line overrides the properties file.
	 * @param name Name of property.
	 * @return The value of the property.
	 */
	public static final String getProperty(String name) {
		String prop = null;
		if (System.getProperty(name) != null) {
			prop = System.getProperty(name);
		} else {
			prop = getProperties().getProperty(name);
		}
		
		if (prop == null)
			throw new RuntimeException("Simulation property '" + name + "' not found");
		
		return prop;
	}

	/**
	 * Determine if the property specifying the precision that metrics should be reported with has been set
	 * @return True, if metric precision has been set
	 */
	public static final boolean isMetricPrecisionSet() {
		return hasProperty(METRIC_PRECISION_PROP);
	}
	
	/**
	 * Get the precision that metrics should be reported with
	 * @return The precision of metrics, or -1 if none has been set
	 */
	public static final int getMetricPrecision() {
		if (isMetricPrecisionSet()) {
			return Integer.parseInt(getProperty(METRIC_PRECISION_PROP));
		}
		return -1;
	}
	
	/**
	 * Round double values to the specified metric precision
	 * @param value
	 * @return
	 */
	public static final double roundToMetricPrecision(double value) {
		if (isMetricPrecisionSet()) {
			return Utility.roundDouble(value, getMetricPrecision());
		}
		return value;
	}
	
}
