package edu.uwo.csd.dcsim.common;

public final class SimTime {

	public static final long seconds(long seconds) {
		return seconds * 1000;
	}
	
	public static final long minutes(long minutes) {
		return seconds(minutes * 60);
	}
	
	public static final long hours(long hours) {
		return minutes(hours  * 60);
	}
	
	public static final long days(long days) {
		return hours(days * 24);
	}
	
	public static final long weeks(long weeks) {
		return days(weeks * 7);
	}
	
}
