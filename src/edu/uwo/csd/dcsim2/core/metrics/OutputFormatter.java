package edu.uwo.csd.dcsim2.core.metrics;

public interface OutputFormatter {

	public static final int NO_ROUNDING = -1;
	
	public String format(double value);
	public String format(double value, int precision);
	
}
