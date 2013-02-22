package edu.uwo.csd.dcsim.core;

import java.util.ArrayList;

public abstract class Event {

	protected Simulation simulation = null;
	private int id = -1;
	private long time;
	private SimulationEventListener target;
	private long sendOrder;
	private ArrayList<EventCallbackListener> callbackListeners = new ArrayList<EventCallbackListener>();
	
	public Event(SimulationEventListener target) {
		this.target = target;
	}
	
	public Event addCallbackListener(EventCallbackListener listener) {
		callbackListeners.add(listener);
		return this;
	}
	
	/**
	 * Provides a hook to run any additional code after the event has been triggered and handled.
	 */
	public void postExecute() {
		//default behaviour is to do nothing
	}
	
	public final void triggerCallback() {
		for (EventCallbackListener listener : callbackListeners) {
			listener.eventCallback(this);
		}
	}
	
	/**
	 * Provides a hook for events to be logged, if desired
	 */
	public void log() {
		//default behaviour is to do nothing
	}
	
	public final void initialize(Simulation simulation) {
		//only initialize if this is the first time the event has been sent
		if (this.simulation == null) {
			this.simulation = simulation;
			id = simulation.nextId(Event.class.toString());
		}
	}
	
	public final int getId() {
		return id;
	}
	
	public final void setTime(long time) {
		this.time = time;
	}
	
	public final long getTime() {
		return time;
	}
	
	public final SimulationEventListener getTarget() {
		return target;
	}
	
	protected final void setSendOrder(long sendOrder) {
		this.sendOrder = sendOrder;
	}
	
	protected final long getSendOrder() {
		return sendOrder;
	}
	
	public final Simulation getSimulation() {
		return simulation;
	}
	
}
