package edu.uwo.csd.dcsim.common;

public class Utility {

	public static double roundDouble(double d) {
		return Math.round(d * 1000000d) / 1000000d;
	}
	
	public static double roundDouble(double d, int precision) {
		return Math.round(d * (Math.pow(10, (double)precision))) / (Math.pow(10, (double)precision));
	}
	
	public static double toKWH(double power) {
		return power / 3600000;
	}
	
	public static double toPercentage(double value) {
		return value * 100;
	}
	
}
