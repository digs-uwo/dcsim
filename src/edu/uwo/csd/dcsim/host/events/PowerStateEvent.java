package edu.uwo.csd.dcsim.host.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.host.*;

public class PowerStateEvent extends Event {

	public enum PowerState {POWER_ON, POWER_OFF, SUSPEND;}
	
	private PowerState state;
	private boolean complete = false;
	
	public PowerStateEvent(Host target, PowerState state) {
		super(target);
		this.state = state;
	}
	
	public PowerStateEvent(Host target, PowerState state, boolean complete) {
		super(target);
		this.state = state;
		this.complete = complete;
	}

	public PowerState getPowerState() {
		return state;
	}
	
	public boolean isComplete() {
		return complete;
	}
	
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	
	
	
}
