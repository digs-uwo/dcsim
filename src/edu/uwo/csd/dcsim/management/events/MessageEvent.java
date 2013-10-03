package edu.uwo.csd.dcsim.management.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.SimulationEventListener;

public class MessageEvent extends Event {

	double messageSize = 0;
	
	public MessageEvent(SimulationEventListener target) {
		super(target);
	}
	
	public void preExecute() {
		//record message count metric
		if (simulation.isRecordingMetrics()) {
			simulation.getSimulationMetrics().getManagementMetrics().addMessage(this);
		}
	}
	
	public void setMessageSize(double messageSize) {
		this.messageSize = messageSize;
	}
	
	public double getMessageSize() {
		return messageSize;
	}

}
