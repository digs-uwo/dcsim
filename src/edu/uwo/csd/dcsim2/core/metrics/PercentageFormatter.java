package edu.uwo.csd.dcsim2.core.metrics;

import edu.uwo.csd.dcsim2.core.Utility;

public class PercentageFormatter implements OutputFormatter {

	@Override
	public String format(double value) {
		return format(value, NO_ROUNDING);
	}

	@Override
	public String format(double value, int precision) {
		value = value * 100;
		if (precision != NO_ROUNDING)
			value = Utility.roundDouble(value, precision);
		
		return value + "%";
	}

}
