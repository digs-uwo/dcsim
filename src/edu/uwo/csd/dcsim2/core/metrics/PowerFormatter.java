package edu.uwo.csd.dcsim2.core.metrics;

import edu.uwo.csd.dcsim2.core.Utility;

public class PowerFormatter implements OutputFormatter {

	@Override
	public String format(double value) {
		return format(value, NO_ROUNDING);
	}

	@Override
	public String format(double value, int decimalPlaces) {
		value = (value / 3600000);
		if (decimalPlaces != NO_ROUNDING)
			value = Utility.roundDouble(value, decimalPlaces);
			
		return value + "kWh"; //convert from watt-seconds to kWh
	}

}
