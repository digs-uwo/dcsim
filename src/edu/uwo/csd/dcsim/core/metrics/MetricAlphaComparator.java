package edu.uwo.csd.dcsim.core.metrics;

import java.util.Comparator;

public class MetricAlphaComparator implements Comparator<Metric> {

	@Override
	public int compare(Metric arg0, Metric arg1) {
		return arg0.getName().compareTo(arg1.getName());
	}
	

}
