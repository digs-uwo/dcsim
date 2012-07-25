package edu.uwo.csd.dcsim.core.metrics;

import edu.uwo.csd.dcsim.common.Utility;

public class PowerFormatter implements OutputFormatter {

	@Override
	public String format(double value) {
		return format(value, NO_ROUNDING);
	}

	@Override
	public String format(double value, int precision) {
		return formatNoUnits(value, precision) + "kWh"; //convert from watt-seconds to kWh
	}

	@Override
	public String formatNoUnits(double value) {
		return formatNoUnits(value, NO_ROUNDING);
	}

	@Override
	public String formatNoUnits(double value, int precision) {
		if (precision != NO_ROUNDING)
			value = Utility.roundDouble(value, precision);
		
		return Double.toString(value); //output value in watt-hours, not kWh
	}

}
