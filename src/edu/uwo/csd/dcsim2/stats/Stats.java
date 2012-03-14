package edu.uwo.csd.dcsim2.stats;

import org.apache.log4j.Logger;

public class Stats {

	private static Logger logger = Logger.getLogger(Stats.class);
	
	private Stats stats = new Stats();
	
	private double powerConsumed = 0;
	private long hostTime = 0;
	
	public Stats getStats() {
		return stats;
	}
	
	private Stats() {
		
	}
	
	public void logStats() {
		
	}
	
	public void logHostStats() {
		
	}

}
