package edu.uwo.csd.dcsim.management.events;

import edu.uwo.csd.dcsim.core.Event;
import edu.uwo.csd.dcsim.core.SimulationEventListener;
import edu.uwo.csd.dcsim.core.metrics.CountMetric;

public class MessageEvent extends Event {

	private static final String MESSAGE_COUNT_METRIC = "messageCount";
	
	public MessageEvent(SimulationEventListener target) {
		super(target);
	}
	
	public void preExecute() {
		//record message count metric
		if (simulation.isRecordingMetrics()) {
			CountMetric.getMetric(simulation, MESSAGE_COUNT_METRIC + "-" + this.getClass().getSimpleName()).incrementCount();
		}
	}

}
