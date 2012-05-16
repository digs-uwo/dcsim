package edu.uwo.csd.dcsim.core;

import java.util.Comparator;

public class EventComparator implements Comparator<Event> {

	@Override
	public int compare(Event e1, Event e2) {
		if (e1.getTime() == e2.getTime()) {
			return (int)(e1.getSendOrder() - e2.getSendOrder());
		}
		
		return (int)(e1.getTime() - e2.getTime());
	}

	
}
