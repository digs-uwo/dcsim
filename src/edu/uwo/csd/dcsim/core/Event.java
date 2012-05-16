package edu.uwo.csd.dcsim.core;

import java.util.HashMap;
import java.util.Map;

public class Event {

	private int type;
	private long time;
	private Object source;
	private SimulationEventListener target;
	private Map<String, Object> data;
	private long sendOrder;
	
	public Event(int type, long time, Object source, SimulationEventListener target) {
		this.type = type;
		this.time = time;
		this.source = source;
		this.target = target;
		data = new HashMap<String, Object>();
	}
	
	public int getType() {
		return type;
	}
	
	public long getTime() {
		return time;
	}
	
	public Object getSource() {
		return source;
	}
	
	public SimulationEventListener getTarget() {
		return target;
	}
	
	public Map<String, Object> getData() {
		return data;
	}
	
	protected void setSendOrder(long sendOrder) {
		this.sendOrder = sendOrder;
	}
	
	protected long getSendOrder() {
		return sendOrder;
	}
	
}
