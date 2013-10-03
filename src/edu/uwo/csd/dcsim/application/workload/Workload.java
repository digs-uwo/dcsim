package edu.uwo.csd.dcsim.application.workload;

import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.core.events.DaemonRunEvent;

/**
 * Represents an external workload submitting work to Applications running in the DataCentre.
 * 
 * @author Michael Tighe
 *
 */
public abstract class Workload implements SimulationEventListener {
	
	protected Simulation simulation;
	protected boolean enabled = true; //has this workload started producing work?

	public Workload(Simulation simulation) {
		
		this.simulation = simulation;
		
		//schedule initial update event
		simulation.sendEvent(new DaemonRunEvent(this));
	}
	
	public Workload(Simulation simulation, boolean enabled) {
		
		this.simulation = simulation;
		this.enabled = enabled;
		
		//schedule initial update event
		simulation.sendEvent(new DaemonRunEvent(this));
	}
	
	/**
	 * Get work awaiting processing
	 * @return
	 */
	protected abstract int getCurrentWorkLevel(); 
	
	/**
	 * Update the current value of the workload level to reflect changes in workload. Return the
	 * time (ms) when the next change in workload will occur.
	 * @return Time (ms) when the next change in workload will occur.
	 */
	protected abstract long updateWorkLevel();
	
	public int getWorkOutputLevel() {
		if (!enabled) return 0;
		return getCurrentWorkLevel();
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	@Override
	public void handleEvent(Event e) {
		if (e instanceof DaemonRunEvent) {
			long nextEventTime = updateWorkLevel();
			if (nextEventTime > simulation.getSimulationTime()) {
				simulation.sendEvent(new DaemonRunEvent(this), nextEventTime);
			}
		}
	}

}
