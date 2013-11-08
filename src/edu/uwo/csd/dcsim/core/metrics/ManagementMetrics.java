package edu.uwo.csd.dcsim.core.metrics;

import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim.common.Tuple;
import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.management.events.MessageEvent;

public class ManagementMetrics extends MetricCollection {

	public ManagementMetrics(Simulation simulation) {
		super(simulation);
		// TODO Auto-generated constructor stub
	}

	Map<Class<? extends MessageEvent>, Long> messageCount = new HashMap<Class<? extends MessageEvent>, Long>();
	Map<Class<? extends MessageEvent>, Double> messageBw = new HashMap<Class<? extends MessageEvent>, Double>();
	Map<Class<?>, Long> migrationCount = new HashMap<Class<?>, Long>();
	long placements;
	
	public enum MigrationType {INTRARACK, INTRACLUSTER, INTERCLUSTER;}
	long intrarack = 0;
	long intracluster = 0;
	long intercluster = 0;
	
	public void addMessage(MessageEvent message) {
		
		long count = 0;
		double bw = 0;
		if (messageCount.containsKey(message.getClass())) {
			count = messageCount.get(message.getClass());
			bw = messageBw.get(message.getClass());
		}
		++count;
		bw += message.getMessageSize();
		
		messageCount.put(message.getClass(), count);
		messageBw.put(message.getClass(), bw);
		
	}
	
	public void addMigration(Class<?> triggeringClass) {
		long count = 0;
		
		if (migrationCount.containsKey(triggeringClass)) {
			count = migrationCount.get(triggeringClass);
		}
		++count;
		
		migrationCount.put(triggeringClass, count);
	}
	
	public void addMigration(Class<?> triggeringClass, MigrationType type) {
		this.addMigration(triggeringClass);
		
		switch (type) {
			case INTRARACK: 	intrarack++;
								break;
								
			case INTRACLUSTER: intracluster++;
								break;
								
			case INTERCLUSTER: intercluster++;
								break;
								
			default: 			break;
		}
	}
	
	public Map<Class<? extends MessageEvent>, Long> getMessageCount() {
		return messageCount;
	}
	
	public double getTotalMessageCount() {
		double total = 0;
		
		for (long l : messageCount.values()) {
			total += l;
		}
		
		return total;
	}
	
	public Map<Class<? extends MessageEvent>, Double> getMessageBw() {
		return messageBw;
	}
	
	public double getTotalMessageBw() {
		double total = 0;
		
		for (Double d : messageBw.values()) {
			total += d;
		}
		
		return total;
	}
	
	public Map<Class<?>, Long> getMigrationCount() {
		return migrationCount;
	}
	
	public double getTotalMigrationCount() {
		double total = 0;
		
		for (Long l : migrationCount.values()) {
			total += l;
		}
		
		return total;
	}

	@Override
	public void completeSimulation() {
		
	}

	@Override
	public void printDefault(Logger out) {
		out.info("-- MANAGEMENT --");
		out.info("Messages");
		for (Entry<Class<? extends MessageEvent>, Long> entry : getMessageCount().entrySet()) {
			out.info("    " + entry.getKey().getName() + ": " + entry.getValue());
		}
		out.info("Message BW");
		for (Entry<Class<? extends MessageEvent>, Double> entry : getMessageBw().entrySet()) {
			out.info("    " + entry.getKey().getName() + ": " + entry.getValue());
		}
		out.info("Migrations");
		for (Entry<Class<?>, Long> entry : getMigrationCount().entrySet()) {
			out.info("    " + entry.getKey().getName() + ": " + entry.getValue());
		}
		out.info("    Intrarack: " + intrarack);
		out.info("    Intracluster: " + intracluster);
		out.info("    Intercluster: " + intercluster);
	}

	@Override
	public List<Tuple<String, Object>> getMetricValues() {
		List<Tuple<String, Object>> metrics = new ArrayList<Tuple<String, Object>>();
		
		for (Entry<Class<? extends MessageEvent>, Long> entry : getMessageCount().entrySet()) {
			metrics.add(new Tuple<String, Object>("messages-" + entry.getKey().getName(),  entry.getValue()));
		}
		for (Entry<Class<? extends MessageEvent>, Double> entry : getMessageBw().entrySet()) {
			metrics.add(new Tuple<String, Object>("messageBW-" + entry.getKey().getName(),  entry.getValue()));
		}
		for (Entry<Class<?>, Long> entry : getMigrationCount().entrySet()) {
			metrics.add(new Tuple<String, Object>("migrations-" + entry.getKey().getName(),  entry.getValue()));
		}
		metrics.add(new Tuple<String, Object>("migrations-intrarack", intrarack));
		metrics.add(new Tuple<String, Object>("migrations-intracluster", intracluster));
		metrics.add(new Tuple<String, Object>("migrations-intercluster", intercluster));
		
		return metrics;
	}
	
}
