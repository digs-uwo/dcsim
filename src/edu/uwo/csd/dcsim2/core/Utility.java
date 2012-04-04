package edu.uwo.csd.dcsim2.core;

import java.util.Random;

public class Utility {

	private static Random random;
	private static long randomSeed;
	
	public static double roundDouble(double d) {
		return Math.round(d * 1000000d) / 1000000d;
	}
	
	public static double roundDouble(double d, int places) {
		return Math.round(d * (Math.pow(10, (double)places))) / (Math.pow(10, (double)places));
	}
	
	public static Random getRandom() {
		if (random == null) {
			random = new Random();
			setRandomSeed(random.nextLong());
		}
		
		return random;
	}
	
	public static long getRandomSeed() {
		return randomSeed;
	}
	
	public static void setRandomSeed(long seed) {
		randomSeed = seed;
		random = new Random(randomSeed);
	}
	
}
