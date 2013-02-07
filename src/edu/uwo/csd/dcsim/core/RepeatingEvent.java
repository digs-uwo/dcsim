package edu.uwo.csd.dcsim.core;

public abstract class RepeatingEvent extends Event implements EventCallbackListener {

	private long interval;
	private boolean running;
	
	public RepeatingEvent(Simulation simulation, SimulationEventListener target, long interval) {
		super(target);
		
		//force early inialization to ensure that "start()" has a simulation reference
		this.initialize(simulation);
		
		this.interval = interval;
		
		this.addCallbackListener(this);
		
		running = false;
	}

	@Override
	public void eventCallback(Event e) {
		//resend event
		if (running != false) {
			simulation.sendEvent(this, simulation.getSimulationTime() + interval);
		}
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}
	
	public long getInterval() {
		return interval;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void stop() {
		running = false;
		simulation.dequeueEvent(this);
	}
	
	public void stopAfterNextExecution() {
		//in this case, simply set the running flag to false to prevent the event from being
		//repeated, but do not remove it from the simulation queue
		running = false;
	}
	
	public void start() {
		start(simulation.getSimulationTime());
	}
	
	public void start(long time) {
		running = true;
		simulation.sendEvent(this, time);
	}
	
}
