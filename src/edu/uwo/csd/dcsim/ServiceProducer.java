package edu.uwo.csd.dcsim;

import java.util.*;

import org.apache.commons.math3.distribution.*;

import edu.uwo.csd.dcsim.application.Service;
import edu.uwo.csd.dcsim.common.Tuple;
import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.SimulationEventListener;
import edu.uwo.csd.dcsim.core.metrics.AggregateMetric;
import edu.uwo.csd.dcsim.vm.VMAllocationRequest;


/**
 * The ServiceProducer class generates new Services and submits them to a data centre based on given parameters. 
 * 
 * @author Michael Tighe
 *
 */
public abstract class ServiceProducer implements SimulationEventListener {

	private final static int SPAWN_SERVICE_EVENT = 1;
	private final static int SHUTDOWN_SERVICE_EVENT = 2;
	private final static int RATE_CHANGE_EVENT = 3;
	private final static String SPAWN_COUNT_METRIC = "servicesSpawned";
	private final static String PLACEMENT_FAIL_METRIC = "servicePlacementsFailed";
	private final static String SHUTDOWN_COUNT_METRIC = "servicesEnded";
	
	DataCentre dcTarget;
	RealDistribution lifespanDist; //if null, create services that do not stop
	ExponentialDistribution arrivalDist;
	List<Tuple<Long, Double>> servicesPerHour;
	int currentRate = -1;
	long startTime = 0;
	
	protected DataCentreSimulation simulation;
	
	public ServiceProducer(DataCentreSimulation simulation, DataCentre dcTarget, List<Tuple<Long, Double>> servicesPerHour) {
		this(simulation, dcTarget, null, servicesPerHour);
	}
	
	public ServiceProducer(DataCentreSimulation simulation, DataCentre dcTarget, double servicesPerHour) {
		this(simulation, dcTarget, null, servicesPerHour);
	}
	
	public ServiceProducer(DataCentreSimulation simulation, DataCentre dcTarget, RealDistribution lifespanDist, List<Tuple<Long, Double>> servicesPerHour) {
		this(simulation, dcTarget, lifespanDist, 0);
		
		this.servicesPerHour = servicesPerHour;
	}
	
	public ServiceProducer(DataCentreSimulation simulation, DataCentre dcTarget, RealDistribution lifespanDist, double servicesPerHour) {
		this.dcTarget = dcTarget;
		this.lifespanDist = lifespanDist;
		this.simulation = simulation;
		
		//reseed the random number generator based on the simulation Random instance to ensure experiment repeatability
		if (lifespanDist != null)
			lifespanDist.reseedRandomGenerator(simulation.getRandom().nextLong());
		
		setArrivalRate(servicesPerHour);
	}
	
	private void setArrivalRate(double servicesPerHour) {
		//create an exponential distribution for generating arrival times
		arrivalDist = new ExponentialDistribution(1 / (servicesPerHour / 60 / 60 / 1000));
		
		//reseed the random number generator based on the simulation Random instance to ensure experiment repeatability
		arrivalDist.reseedRandomGenerator(simulation.getRandom().nextLong());

		simulation.getLogger().debug("Service Arrival Rate set to " + servicesPerHour + " services-per-hour");
	}
	
	private void sendNextRateChangeEvent() {
		
		//if we are on the last rate change, add the duration of the rate change 'trace' to the start time to loop
		if (currentRate == servicesPerHour.size() - 1) {
			startTime = startTime + servicesPerHour.get(currentRate).a;
		}
		
		long nextEvent = (servicesPerHour.get((currentRate + 1) % servicesPerHour.size()).a) + startTime;
				
		simulation.sendEvent(new Event(RATE_CHANGE_EVENT, nextEvent, this, this));
	}
	
	public abstract Service buildService();
	
	private void spawnService() {
		Service service = buildService();
		
		
		ArrayList<VMAllocationRequest> vmAllocationRequests = service.createInitialVmRequests();
		
		simulation.getLogger().debug("Created New Service");
		
		AggregateMetric.getMetric(simulation, SPAWN_COUNT_METRIC).addValue(1);
		
		if (dcTarget.getVMPlacementPolicy().submitVMs(vmAllocationRequests)) {
			
			//send event to trigger service shutdown on lifespan + currentTime
			if (lifespanDist != null) {
				long lifeSpan = (long)Math.round(lifespanDist.sample());
				Event shutdownEvent = new Event(SHUTDOWN_SERVICE_EVENT, simulation.getSimulationTime() + lifeSpan, this, this);
				shutdownEvent.getData().put("service", service);
				simulation.sendEvent(shutdownEvent);
			}
			
		} else {
			simulation.getLogger().debug("Service Placement Failed");
			AggregateMetric.getMetric(simulation, PLACEMENT_FAIL_METRIC).addValue(1);
		}

	}
	
	private void shutdownService(Service service) {
		
		//remove the workload from the simulation in order to stop incoming work to the service
		simulation.removeWorkload(service.getWorkload());
		
		//check to see if the service is ready to shutdown (i.e. no VMs are migrating)
		if (service.canShutdown()) {
			service.shutdownService();
			AggregateMetric.getMetric(simulation, SHUTDOWN_COUNT_METRIC).addValue(1);
			
			simulation.getLogger().debug("Shutdown Service");
		}
		/*
		 * some VMs must be migrating, so we delay shutdown to wait for migrations to end. No further work will be processed
		 * since we removed the workload from the simulation
		 */
		else {
			long delay = (long)Math.round(simulation.getRandom().nextDouble() * 30000 + 15000);
			Event shutdownEvent = new Event(SHUTDOWN_SERVICE_EVENT, simulation.getSimulationTime() + delay, this, this);
			shutdownEvent.getData().put("service", service);
			simulation.sendEvent(shutdownEvent);
		}

	}
	
	public void start() {
		startTime = simulation.getSimulationTime();
		sendNextSpawnEvent();
		sendNextRateChangeEvent();
	}
	
	private void sendNextSpawnEvent() {
		//Check to make sure we are spawning services. Setting a servicesPerHour rate of 0 results in the mean of the distribution being positive infinity
		if (!(arrivalDist.getMean() == Double.POSITIVE_INFINITY)) {
			long nextSpawn =  simulation.getSimulationTime() + (long)Math.round(arrivalDist.sample());
			
			Event spawnEvent = new Event(SPAWN_SERVICE_EVENT, nextSpawn, this, this);
			spawnEvent.getData().put("currentRate", new Integer(currentRate));
			simulation.sendEvent(spawnEvent);
		}
	}

	
	@Override
	public final void handleEvent(Event e) {
		if (e.getType() == SPAWN_SERVICE_EVENT) {
			/*
			 * We must verify that this event was sent during the current rate, otherwise we may have two separate "threads" of
			 * service spawning events
			 */
			int rate = (Integer)e.getData().get("currentRate"); 
			if (rate == currentRate) {
				spawnService();
				sendNextSpawnEvent();
			}
		} else if (e.getType() == SHUTDOWN_SERVICE_EVENT) {
			shutdownService((Service)e.getData().get("service"));
		} else if (e.getType() == RATE_CHANGE_EVENT) {
			int previousRate = currentRate;
			currentRate = (currentRate + 1) % servicesPerHour.size();
			
			//check to see if the previous rate is equal to the current rate, which would be done to achieve a constant rate at the end of the loop
			if (previousRate == -1 || !servicesPerHour.get(currentRate).b.equals(servicesPerHour.get(previousRate).b)) {
				setArrivalRate(servicesPerHour.get(currentRate).b);
				sendNextSpawnEvent(); //send a new spawn event in case the previous rate was very slow or 0
			}
			
			sendNextRateChangeEvent();
		}
	}
	
}
