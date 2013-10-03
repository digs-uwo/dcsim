package edu.uwo.csd.dcsim.management.events;

import java.util.ArrayList;

import edu.uwo.csd.dcsim.application.Application;
import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.management.AutonomicManager;

public class ApplicationPlacementEvent extends Event {

	private ArrayList<Application> applications;
	private boolean failed = false;
	
	public ApplicationPlacementEvent(AutonomicManager target, Application application) {
		super(target);
		
		applications = new ArrayList<Application>();
		applications.add(application);
		
	}
	
	public ApplicationPlacementEvent(AutonomicManager target, ArrayList<Application> applications) {
		super(target);
		
		this.applications = applications;
	}

	public ArrayList<Application> getApplications() {
		return applications;
	}
	
	public void setFailed(boolean failed) {
		this.failed = failed;
	}
	
	public boolean isFailed() {
		return failed;
	}

}
