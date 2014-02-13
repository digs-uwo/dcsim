package edu.uwo.csd.dcsim.core;

import java.util.Comparator;

public class EventComparator implements Comparator<Event> {

	@Override
	public int compare(Event e1, Event e2) {
        if (e1.getTime() == e2.getTime()) {
			return new Long(e1.getSendOrder()).compareTo(e2.getSendOrder());
		}

		return new Long(e1.getTime()).compareTo(e2.getTime());
	}

	
}
