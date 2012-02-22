package edu.uwo.csd.dcsim2.logging;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.PatternParser;

public class SimulationPatternLayout extends PatternLayout {

	@Override
	protected PatternParser createPatternParser(String pattern) {
		return new SimulationPatternParser(pattern);
	}
		
}
