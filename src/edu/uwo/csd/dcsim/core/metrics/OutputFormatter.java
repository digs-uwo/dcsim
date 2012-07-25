package edu.uwo.csd.dcsim.core.metrics;

public interface OutputFormatter {

	public static final int NO_ROUNDING = -1;
	
	public String format(double value);
	public String format(double value, int precision);
	public String formatNoUnits(double value);
	public String formatNoUnits(double value, int precision);
	
}
