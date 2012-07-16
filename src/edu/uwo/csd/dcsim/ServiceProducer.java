package edu.uwo.csd.dcsim;

import java.util.ArrayList;

import org.apache.commons.math3.distribution.*;

import edu.uwo.csd.dcsim.application.Service;
import edu.uwo.csd.dcsim.core.Daemon;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.vm.VMAllocationRequest;


/**
 * The ServiceProducer class generates new Services and submits them to a data centre based on given parameters. 
 * 
 * @author Michael Tighe
 *
 */
public abstract class ServiceProducer implements Daemon {

	DataCentre dcTarget;
	RealDistribution lifespanDist;
	long interArrivalTime;
	ExponentialDistribution arrivalDist;
	
	Simulation simulation;
	
	public ServiceProducer(Simulation simulation, DataCentre dcTarget, RealDistribution lifespanDist, long interArrivalTime) {
		this.dcTarget = dcTarget;
		this.lifespanDist = lifespanDist;
		this.interArrivalTime = interArrivalTime;
		this.simulation = simulation;
		
		//create an exponential distribution for generating arrival times
		arrivalDist = new ExponentialDistribution(interArrivalTime);
		
		//reseed the random number generators based on the simulation Random instance to ensure experiment repeatability
		lifespanDist.reseedRandomGenerator(simulation.getRandom().nextLong());
		arrivalDist.reseedRandomGenerator(simulation.getRandom().nextLong());
		
	}
	
	public abstract Service buildService();
	
	private void spawnService() {
		Service service = buildService();
		long lifeSpan = (long)Math.round(lifespanDist.sample());
		
		ArrayList<VMAllocationRequest> vmAllocationRequests = service.createInitialVmRequests();
		
		dcTarget.getVMPlacementPolicy().submitVMs(vmAllocationRequests);
		
		//send event to trigger service shutdown on lifespan + currentTime
	}
	
	@Override
	public void start(Simulation simulation) {
		
	}

	@Override
	public void run(Simulation simulation) {
		
	}

	@Override
	public void stop(Simulation simulation) {

	}
	
}
