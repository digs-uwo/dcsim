package edu.uwo.csd.dcsim.core;

import java.util.ArrayList;

public abstract class Event {

	protected Simulation simulation = null;
	private int id = -1;
	private long time;
	private SimulationEventListener target;
	private long sendOrder;
	private ArrayList<EventCallbackListener> callbackListeners = new ArrayList<EventCallbackListener>();
	
	private boolean waitOnNextEvent = false; //true if we are waiting for another event to run
	
	public Event(SimulationEventListener target) {
		this.target = target;
	}
	
	public final Event addCallbackListener(EventCallbackListener listener) {
		callbackListeners.add(listener);
		return this;
	}
	
	public final void addEventInSequence(Event nextEvent) {
		//flag that we are waiting to trigger postExecute, log, and callbackListeners until the next event is done
		waitOnNextEvent = true;
		
		//add a listener to trigger methods once the next event completes. This can cause a cascade back through several sequenced events.
		nextEvent.addCallbackListener(new EventCallbackListener() {

			@Override
			public void eventCallback(Event e) {
				waitOnNextEvent = false;
				postExecute();
				triggerCallback();
			}
			
		});
	}
	
	public final void cancelEventInSequence() {
		waitOnNextEvent = false;
		postExecute();
		triggerCallback();
	}
	
	/**
	 * Provides a hook to run any additional code after the event has been triggered and handled.
	 */
	public void postExecute() {
		//default behaviour is to do nothing, designed to be overridden
	}
	
	public final void triggerPostExecute() {
		if (!waitOnNextEvent) {
			postExecute();
		}
	}
	
	
	
	public final void triggerCallback() {
		if (!waitOnNextEvent) {
			for (EventCallbackListener listener : callbackListeners) {
				listener.eventCallback(this);
			}
		}
	}
	
	/**
	 * Provides a hook to run code just before this event is executed
	 */
	public void preExecute() {
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
