package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.common.Utility;

public class PercentageFormatter implements OutputFormatter {

	@Override
	public String format(double value) {
		return format(value, NO_ROUNDING);
	}

	@Override
	public String format(double value, int precision) {
		return formatNoUnits(value, precision) + "%";
	}

	@Override
	public String formatNoUnits(double value) {
		return formatNoUnits(value, NO_ROUNDING);
	}

	@Override
	public String formatNoUnits(double value, int precision) {
		value = value * 100;
		if (precision != NO_ROUNDING)
			value = Utility.roundDouble(value, precision);
		
		return Double.toString(value);
	}

}
