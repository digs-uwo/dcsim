package edu.uwo.csd.dcsim.core;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import edu.uwo.csd.dcsim.DataCentre;
import edu.uwo.csd.dcsim.application.Application;
//import edu.uwo.csd.dcsim.common.SimTime;
import edu.uwo.csd.dcsim.common.Utility;
import edu.uwo.csd.dcsim.core.events.*;
import edu.uwo.csd.dcsim.core.metrics.*;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.logging.*;

import java.util.*;
import java.io.*;

/**
 * Simulation is a simulation of a data centre, which consists of a collection of DataCentres containing Hosts, which
 * host VMs running Applications. Simulation is the main object that controls the execution of the simulation.
 * 
 * All DataCentres in the simulation must be added to this object, as well as all Workload objects feeding applications within the
 * simulation.
 * 
 * @author Michael Tighe
 *
 */
public class Simulation implements SimulationEventListener {
	
	//Logging defaults
	public static final String DEFAULT_CONSOLE_CONVERSION_PATTERN = "%-5p %-50c - %m%n";
	public static final String DEFAULT_MAINFILE_CONVERSION_PATTERN = "%-5p %-50c - %m%n";
	public static final String DEFAULT_MAINFILE_FILE_NAME = "main-%d.log";
	
	public static final String DEFAULT_LOGGER_CONVERSION_PATTERN = "%-10s %-5p - %m%n";
	public static final String DEFAULT_LOGGER_DATE_FORMAT = "yyyy_MM_dd'-'HH_mm_ss";
	public static final String DEFAULT_LOGGER_FILE_NAME = "%n-%d.log";
	
	//Trace logging defautls
	public static final String DEFAULT_TRACE_CONVERSION_PATTERN = "%s,%m%n";
	public static final String DEFAULT_TRACE_DATE_FORMAT = "yyyy_MM_dd'-'HH_mm_ss";
	public static final String DEFAULT_TRACE_FILE_NAME = "%n-%d.trace";
	
	//directory constants
	private static String homeDirectory = null;
	private static String LOG_DIRECTORY = "/log";
	private static String CONFIG_DIRECTORY = "/config";
	
	//the name of property in the simulation properties file that defines the precision with which to report metrics
	private static String METRIC_PRECISION_PROP = "metricPrecision";
	
	private static ConsoleAppender consoleAppender;
	private static LogPerRunFileAppender mainFileAppender;
	protected final Logger logger; //logger
	protected final Logger traceLogger; //logger for trace file
	protected boolean enableTrace;
	
	private static Properties properties; //simulation properties
	
	private String name; 						//name of the simulation
	private PriorityQueue<Event> eventQueue;	//contains all future events, in order
	private long simulationTime; 				//current time, in milliseconds
	private long lastUpdate; 					//in milliseconds
	private long duration;						//duration of the entire simulation, at which point it terminates
	private long metricRecordStart;
	private boolean recordingMetrics;
	private long eventSendCount = 0;
	protected SimulationMetrics simulationMetrics;
		
	private Random random;
	private long randomSeed;
	
	private Map<String, Integer> nextIdMap = new HashMap<String, Integer>();
	
	private static Logger simLogger = Logger.getLogger(Simulation.class);
	
	private boolean complete = false;
	
	//Datacentre specific variables
	private ArrayList<DataCentre> datacentres = new ArrayList<DataCentre>(); //collection of datacentres within the simulation
	private Set<Application> applications = new HashSet<Application>();
	
	public static final void initializeLogging() {
		
		PatternLayout consoleLayout = new PatternLayout();
		consoleLayout.setConversionPattern(DEFAULT_CONSOLE_CONVERSION_PATTERN);
		consoleAppender = new ConsoleAppender();
		consoleAppender.setLayout(consoleLayout);
		consoleAppender.setWriter(new OutputStreamWriter(System.out));
		consoleAppender.setName("console");

		//configure root logger
		Logger.getRootLogger().addAppender(consoleAppender);
		Logger.getRootLogger().setLevel(Level.INFO);
		
		//configure main file root logging
		boolean enableConsoleLogFile = false;
		if (getProperties().getProperty("enableConsoleLogFile") != null) {
			enableConsoleLogFile = Boolean.parseBoolean(getProperties().getProperty("enableConsoleLogFile"));
		}
		
		if (enableConsoleLogFile) {
			PatternLayout consoleFileLayout = new PatternLayout();
			consoleFileLayout.setConversionPattern(DEFAULT_MAINFILE_CONVERSION_PATTERN);
			mainFileAppender = new LogPerRunFileAppender();
			mainFileAppender.setLayout(consoleFileLayout);
			mainFileAppender.setFile(getLogDirectory() + "/" + DEFAULT_MAINFILE_FILE_NAME);
			mainFileAppender.setDateFormat(DEFAULT_LOGGER_DATE_FORMAT);
			mainFileAppender.setName("mainFile");
			mainFileAppender.activateOptions();
			
			Logger.getRootLogger().addAppender(mainFileAppender);
		}
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
	
		Logger.getLogger("simLogger").setAdditivity(false);
		
		//default the simLogger to OFF until we attach an appender
		Logger.getLogger("simLogger").setLevel(Level.OFF);
		
		boolean enableFileLogging = true;
		if (getProperties().getProperty("enableSimulationLogFile") != null) {
			enableFileLogging = Boolean.parseBoolean(getProperties().getProperty("enableSimulationLogFile"));
		}
		
		String conversionPattern = DEFAULT_LOGGER_CONVERSION_PATTERN;
		String dateFormat = DEFAULT_LOGGER_DATE_FORMAT;
		String fileName = DEFAULT_LOGGER_FILE_NAME;

		if (enableFileLogging) {
			
			Logger.getLogger("simLogger").setLevel(Level.DEBUG);
			
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
		
		//if detailedConsole
		if(getProperties().getProperty("detailedConsole") != null &&
				Boolean.parseBoolean(getProperties().getProperty("detailedConsole"))) {
			
			Logger.getLogger("simLogger").setLevel(Level.DEBUG);			
			logger.addAppender(consoleAppender);
			
			//if outputting console to main file
			if (getProperties().getProperty("enableConsoleLogFile") != null &&
					Boolean.parseBoolean(getProperties().getProperty("enableConsoleLogFile")) &&
					mainFileAppender != null) {
				logger.addAppender(mainFileAppender);
			}
		}
		
		//configure simulation trace logger
		enableTrace = false;
		if (getProperties().getProperty("enableTrace") != null) {
			enableTrace = Boolean.parseBoolean(getProperties().getProperty("enableTrace"));
		}

		//create a logger between root and individual trace loggers with additivity false, to
		//prevent trace logs from being passed to the root logger
		Logger.getLogger("traceLogger").setAdditivity(false);
		
		traceLogger = Logger.getLogger("traceLogger." + name);
		if (enableTrace) {
			traceLogger.setLevel(Level.INFO);
			
			SimulationFileAppender simAppender = new SimulationFileAppender();
			
			SimulationPatternLayout patternLayout = new SimulationPatternLayout(this);
			patternLayout.setConversionPattern(DEFAULT_TRACE_CONVERSION_PATTERN);
			simAppender.setLayout(patternLayout);
			simAppender.setSimName(name);
			simAppender.setDateFormat(DEFAULT_TRACE_DATE_FORMAT);
			simAppender.setFile(getLogDirectory() + "/" + DEFAULT_TRACE_FILE_NAME);
			simAppender.activateOptions();
			traceLogger.addAppender(simAppender);
			
		} else {
			traceLogger.setLevel(Level.OFF);
		}
		
		//initialize Random
		setRandomSeed(new Random().nextLong());
		
		simulationMetrics = new SimulationMetrics(this);
		
	}
	
	public final SimulationMetrics run(long duration, long metricRecordStart) {
		
		//ensure this simulation hasn't been run yet
		if (complete)
			throw new IllegalStateException("Simulation has already been run");
		
		//Initialize
		ArrayList<Host> hosts = getHostList();
		
		Event e;
		
		//configure simulation duration
		this.duration = duration;
		sendEvent(new TerminateSimulationEvent(this), duration); //this event runs at the last possible time in the simulation to ensure simulation updates
		
		if (metricRecordStart > 0) {
			recordingMetrics = false;
			this.metricRecordStart = metricRecordStart;
			sendEvent(new RecordMetricsEvent(this), metricRecordStart);
		} else {
			recordingMetrics = true;
		}
		
		simLogger.info("Starting DCSim");
		simLogger.info("Random Seed: " + this.getRandomSeed());
		
		//main event loop
		while (!eventQueue.isEmpty() && simulationTime < duration) {
			
//			System.out.println(SimTime.toHumanReadable(simulationTime));
			
			//peak at next event
			e = eventQueue.peek();
						
			//make sure that the event is in the future
			if (e.getTime() < simulationTime)
				throw new IllegalStateException("Encountered event (" + e.getClass() + ") with time < current simulation time");
			
			if (e.getTime() == simulationTime && e.getTime() != 0)
				throw new IllegalStateException("Encountered event with time == current simulation time when advance in time was expected. This should never occur.");
			
			//ensure that we are not at time 0. If we are, we do not need to advance the simulation yet, only run events
			if (e.getTime() != 0) {
				//Simulation time is advancing
				
				//schedule/allocate resources
				scheduleResources(hosts);

				//revise/amend
				postScheduling();
				
				//get the next event, which may have changed during the revise step
				e = eventQueue.peek();
				
				//make sure that the event is in the future
				if (e.getTime() < simulationTime)
					throw new IllegalStateException("Encountered post-scheduling event (" + e.getClass() + ") with time < current simulation time");
				
				//advance to time e.getTime()
				lastUpdate = simulationTime;
				simulationTime = e.getTime();
				advanceSimulation(hosts);

				if (this.isRecordingMetrics()) {	
					//update host metrics
					simulationMetrics.getHostMetrics().recordHostMetrics(hosts);
					
					//update application metrics
					simulationMetrics.getApplicationMetrics().recordApplicationMetrics(applications);
					
				}
			}

			//log current state
			for (DataCentre dc : datacentres) {
				dc.logState();			
			}
			
			//execute current events
			while (!eventQueue.isEmpty() && (eventQueue.peek().getTime() == simulationTime)) {
				e = eventQueue.poll();

				e.preExecute();
				e.getTarget().handleEvent(e);	//the target handles the event
				e.triggerPostExecute();				//run any additional logic required by the event
				e.triggerCallback();			//trigger any objects awaiting a post-event callback
			}
			
		}
		
		//Simulation is now completed
		simulationMetrics.completeSimulation();
		completeSimulation(duration);
		
		simLogger.info("");
		simLogger.info("Completed simulation " + name);
		
		complete = true;

		return simulationMetrics;
		
	}

	private void scheduleResources(ArrayList<Host> hosts) {	
				
		//reset host schedulers
		for (Host host : hosts) {
			//reset all scheduled resources to zero (subsequently, hosts not 'ON' will not be scheduled and will remain at zero)
			host.getResourceScheduler().resetScheduling();
		}
		
		//initialize Applications (reset scheduled/demand, set scheduled = size)
		for (Application application : applications) {
			application.initializeScheduling();
		}

		//update application demands (includes solving MVA and updating cpu demand)
		for (Application application : applications) {
			application.updateDemand();
		}
		
		
		//while not done
		boolean done = false;
		while (!done) {
			done = true;
			//schedule cpu on all hosts (in no order)
			for (Host host : hosts) {
				//schedule cpu
				if (host.getState() == Host.HostState.ON) {
					host.getResourceScheduler().scheduleResources();
				}
			}
			for (Application application : applications) {
				boolean appUpdate = application.updateDemand(); 
				done = done && !appUpdate; //stop when no calls to updateDemand result in changes  
			}
		}
		
		

	}
	
	private void postScheduling() {
		for (Application app : applications) {
			app.postScheduling();
		}
	}
	
	/**
	 * Run all applications up to current simulation time
	 * 
	 * @param hosts
	 */
	private void advanceSimulation(ArrayList<Host> hosts) {
		//execute all applications up to the current simulation time
		for (Application app : applications) {
			app.advanceSimulation();
		}

	}
	
	public void completeSimulation(long duration) {
		logger.info("DCSim Simulation Complete");
		
		//log simulation time
		double simTime = this.getDuration();
		double recordedTime = this.getRecordingDuration();
		String simUnits = "ms";
		if (simTime >= 864000000) { //>= 10 days
			simTime = simTime / 86400000;
			recordedTime = recordedTime / 86400000;
			simUnits = " days";
		} else if (simTime >= 7200000) { //>= 2 hours
			simTime = simTime / 3600000;
			recordedTime = recordedTime / 3600000;
			simUnits = "hrs";
		} else if (simTime >= 600000) { //>= 2 minutes
			simTime = simTime / 60000d;
			recordedTime = recordedTime / 60000d;
			simUnits = "mins";
		} else if (simTime >= 10000) { //>= 10 seconds
			simTime = simTime / 1000d;
			recordedTime = recordedTime / 1000d;
			simUnits = "s";
		}
		logger.info("Simulation Time: " + simTime + simUnits);
		logger.info("Recorded Time: " + recordedTime + simUnits);
	
	}
	
	public final long sendEvent(Event event, long time) {
		event.initialize(this);
		event.setSendOrder(++eventSendCount);
		event.setTime(time);
		eventQueue.add(event);
		
		return event.getSendOrder();
	}
	
	public final long sendEvent(Event event) {
		return sendEvent(event, getSimulationTime());
	}
	
	public final void dequeueEvent(Event event) {
		eventQueue.remove(event);
	}
	
	@Override
	public final void handleEvent(Event e) {
		
		if (e instanceof TerminateSimulationEvent) {
			//nothing to do, just let the simulation terminate
		} else if (e instanceof RecordMetricsEvent) {
			recordingMetrics = true;
		} else if (e instanceof RunMonitorsEvent) {
			//nothing to do, this will ensure that the simulation processes the monitors in case no other event is scheduled
		} else {
			throw new RuntimeException("Simulation received unknown event type");
		}

	}
	
	public final Logger getLogger() {
		return logger;
	}
	
	public final Logger getTraceLogger() {
		return traceLogger;
	}
	
	public final boolean isTraceEnabled() {
		return enableTrace;
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
	
	public final SimulationMetrics getSimulationMetrics() {
		return simulationMetrics;
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
	
	/**
	 * Add a DataCentre to the simulation
	 * @param dc
	 */
	public void addDatacentre(DataCentre dc) {
		datacentres.add(dc);
	}
	
	public void addApplication(Application application) {
		applications.add(application);
	}
	
	public void removeApplication(Application application) {
		applications.remove(application);
	}
	
	/**
	 * Get a list of all of the Hosts within the simulation
	 * @return
	 */
	private ArrayList<Host> getHostList() {
		
		int nHosts = 0;
		for (DataCentre dc : datacentres)
			nHosts += dc.getHosts().size();
		
		ArrayList<Host> hosts = new ArrayList<Host>(nHosts);
		
		for (DataCentre dc : datacentres) {
			hosts.addAll(dc.getHosts());
		}
		
		return hosts;
	}

	public ArrayList<DataCentre> getDataCentres(){
		return datacentres;
	}
	
}
