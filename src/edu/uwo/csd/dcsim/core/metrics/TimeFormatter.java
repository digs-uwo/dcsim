package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.common.Utility;

public class TimeFormatter implements OutputFormatter {

	public enum TimeUnit {MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS;}
	
	private TimeUnit from;
	private TimeUnit to;
	
	public TimeFormatter(TimeUnit from, TimeUnit to) {
		this.from = from;
		this.to = to;
	}
	
	@Override
	public String format(double value) {
		return format(value, NO_ROUNDING);
	}

	@Override
	public String format(double value, int precision) {
		String unit = "s";
		
		switch (from) {
			case MILLISECONDS:
				value = value / 1000;
				break;
			case MINUTES:
				value = value * 60;
				break;
			case HOURS:
				value = value * 3600;
				break;
			case DAYS:
				value = value * 86400;
		}
		
		switch (to) {
			case MILLISECONDS:
				value = value * 1000;
				unit = "ms";
				break;
			case MINUTES:
				value = value / 60;
				unit = "mins";
				break;
			case HOURS:
				value = value / 3600;
				unit = "hrs";
				break;
			case DAYS:
				value = value / 86400;
				unit = " days";
				break;
		}
		
		if (precision != NO_ROUNDING) {
			value = Utility.roundDouble(value, precision);
		}
		
		return value + unit;
		
	}

	@Override
	public String formatNoUnits(double value) {
		return formatNoUnits(value, NO_ROUNDING);
	}

	@Override
	public String formatNoUnits(double value, int precision) {
		if (precision != NO_ROUNDING) {
			value = Utility.roundDouble(value, precision);
		}
		
		return Double.toString(value); //without printing units, simply output result in ms
	}

}
