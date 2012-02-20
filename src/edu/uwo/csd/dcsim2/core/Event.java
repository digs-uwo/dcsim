package edu.uwo.csd.dcsim2.core;

import java.util.HashMap;
import java.util.Map;

public class Event {

	private int type;
	private long time;
	private SimulationEntity source;
	private SimulationEntity target;
	private Map<String, Object> data;
	private long sendOrder;
	
	public Event(int type, long time, SimulationEntity source, SimulationEntity target) {
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
	
	public SimulationEntity getSource() {
		return source;
	}
	
	public SimulationEntity getTarget() {
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
