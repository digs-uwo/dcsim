package edu.uwo.csd.dcsim2.core;

public class Utility {

	public static double roundDouble(double d) {
		return Math.round(d * 1000000d) / 1000000d;
	}
	
	public static double roundDouble(double d, int places) {
		return Math.round(d * (Math.pow(10, (double)places))) / (Math.pow(10, (double)places));
	}
	
}
