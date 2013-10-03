package edu.uwo.csd.dcsim.application;

import java.util.*;

import org.apache.commons.math3.distribution.*;

import edu.uwo.csd.dcsim.application.events.*;
import edu.uwo.csd.dcsim.common.Tuple;
import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.EventCallbackListener;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.core.SimulationEventListener;
import edu.uwo.csd.dcsim.core.events.DaemonRunEvent;
import edu.uwo.csd.dcsim.management.AutonomicManager;
import edu.uwo.csd.dcsim.management.events.ApplicationPlacementEvent;

/**
 * The ApplicationGenerator class generates new Applications and submits them to a data centre based on given parameters. 
 * 
 * @author Michael Tighe
 *
 */
public abstract class ApplicationGenerator implements SimulationEventListener {

	AutonomicManager dcTarget;
	RealDistribution lifespanDist; //if null, create services that do not stop
	ExponentialDistribution arrivalDist;
	List<Tuple<Long, Double>> applicationsPerHour;
	int currentRate = -1;
	long startTime = 0;
	
	protected Simulation simulation;
	
	public ApplicationGenerator(Simulation simulation, AutonomicManager dcTarget, List<Tuple<Long, Double>> applicationsPerHour) {
		this(simulation, dcTarget, null, applicationsPerHour);
	}
	
	public ApplicationGenerator(Simulation simulation, AutonomicManager dcTarget, double applicationsPerHour) {
		this(simulation, dcTarget, null, applicationsPerHour);
	}
	
	public ApplicationGenerator(Simulation simulation, AutonomicManager dcTarget, RealDistribution lifespanDist, List<Tuple<Long, Double>> applicationsPerHour) {
		this(simulation, dcTarget, lifespanDist, 0);
		
		this.applicationsPerHour = applicationsPerHour;
	}
	
	public ApplicationGenerator(Simulation simulation, AutonomicManager dcTarget, RealDistribution lifespanDist, double applicationsPerHour) {
		this.dcTarget = dcTarget;
		this.lifespanDist = lifespanDist;
		this.simulation = simulation;
		
		//reseed the random number generator based on the simulation Random instance to ensure experiment repeatability
		if (lifespanDist != null)
			lifespanDist.reseedRandomGenerator(simulation.getRandom().nextLong());
		
		setArrivalRate(applicationsPerHour);
	}
	
	private void setArrivalRate(double applicationsPerHour) {
		//create an exponential distribution for generating arrival times
		arrivalDist = new ExponentialDistribution(1 / (applicationsPerHour / 60 / 60 / 1000));
		
		//reseed the random number generator based on the simulation Random instance to ensure experiment repeatability
		arrivalDist.reseedRandomGenerator(simulation.getRandom().nextLong());

		simulation.getLogger().debug("Service Arrival Rate set to " + applicationsPerHour + " services-per-hour");
	}
	
	private void sendNextRateChangeEvent() {
		
		//if we are on the last rate change, add the duration of the rate change 'trace' to the start time to loop
		if (currentRate == applicationsPerHour.size() - 1) {
			startTime = startTime + applicationsPerHour.get(currentRate).a;
		}
		
		long nextEvent = (applicationsPerHour.get((currentRate + 1) % applicationsPerHour.size()).a) + startTime;
				
		simulation.sendEvent(new DaemonRunEvent(this), nextEvent);
	}
	
	public abstract Application buildApplication();
	
	private void spawnApplication() {
		Application application = buildApplication();
			
		simulation.getLogger().debug("Created New Application");
		
		simulation.getSimulationMetrics().getApplicationMetrics().incrementApplicationsSpawned();

		ApplicationPlacementEvent placementEvent = new ApplicationPlacementEvent(dcTarget, application);
		
		//add a callback handler to record placement success/failure
		placementEvent.addCallbackListener(new ApplicationSpawnCallbackHandler(application));

		simulation.sendEvent(placementEvent);
		
	}
	
	private void shutdownApplication(Application application) {
		
		//remove the application from the simulation
		simulation.removeApplication(application);
		
		//check to see if the application is ready to shutdown (i.e. no VMs are migrating)
		if (application.canShutdown()) {
			application.shutdownApplication(dcTarget, simulation);
			simulation.getSimulationMetrics().getApplicationMetrics().incrementApplicationShutdown();
			
			simulation.getLogger().debug("Shutdown Application");
		}
		/*
		 * some VMs must be migrating, so we delay shutdown to wait for migrations to end. No further work will be processed
		 * since we removed the application from the simulation
		 */
		else {
			long delay = (long)Math.round(simulation.getRandom().nextDouble() * 30000 + 15000);
			simulation.sendEvent(new ShutdownApplicationEvent(this, application), simulation.getSimulationTime() + delay);
		}

	}
	
	public void start() {
		startTime = simulation.getSimulationTime();
		sendNextSpawnEvent();
		sendNextRateChangeEvent();
	}
	
	private void sendNextSpawnEvent() {
		//Check to make sure we are spawning applications. Setting an applicationsPerHour rate of 0 results in the mean of the distribution being positive infinity
		if (!(arrivalDist.getMean() == Double.POSITIVE_INFINITY)) {
			long nextSpawn =  simulation.getSimulationTime() + (long)Math.round(arrivalDist.sample());
			
			simulation.sendEvent(new SpawnApplicationEvent(this, currentRate), nextSpawn);
		}
	}

	
	@Override
	public final void handleEvent(Event e) {
		if (e instanceof SpawnApplicationEvent) {
			/*
			 * We must verify that this event was sent during the current rate, otherwise we may have two separate "threads" of
			 * application spawning events
			 */
			SpawnApplicationEvent spawnEvent = (SpawnApplicationEvent)e; 
			if (spawnEvent.getCurrentRate() == currentRate) {
				spawnApplication();
				sendNextSpawnEvent();
			}
		} else if (e instanceof ShutdownApplicationEvent) {
			ShutdownApplicationEvent shutdownEvent = (ShutdownApplicationEvent)e;
			shutdownApplication(shutdownEvent.getService());
		} else if (e instanceof DaemonRunEvent) {
			int previousRate = currentRate;
			currentRate = (currentRate + 1) % applicationsPerHour.size();
			
			//check to see if the previous rate is equal to the current rate, which would be done to achieve a constant rate at the end of the loop
			if (previousRate == -1 || !applicationsPerHour.get(currentRate).b.equals(applicationsPerHour.get(previousRate).b)) {
				setArrivalRate(applicationsPerHour.get(currentRate).b);
				sendNextSpawnEvent(); //send a new spawn event in case the previous rate was very slow or 0
			}
			
			sendNextRateChangeEvent();
		}
	}
	
	public class ApplicationSpawnCallbackHandler implements EventCallbackListener {

		private Application application;
		private boolean triggered = false;
		
		public ApplicationSpawnCallbackHandler(Application service) {
			this.application = service;
		}
		
		@Override
		public void eventCallback(Event e) {

			if (triggered) throw new RuntimeException("What?");
			triggered = true;
			ApplicationPlacementEvent placementEvent = (ApplicationPlacementEvent)e;
			
			if (!placementEvent.isFailed()) {
				//send event to trigger application shutdown on lifespan + currentTime
				if (lifespanDist != null) {
					long lifeSpan = (long)Math.round(lifespanDist.sample());
					
					simulation.sendEvent(new ShutdownApplicationEvent(ApplicationGenerator.this, application), simulation.getSimulationTime() + lifeSpan);
				}
			}
		}
		
	}
}
