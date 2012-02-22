package edu.uwo.csd.dcsim2.logging;

import org.apache.log4j.helpers.PatternParser;

public class SimulationPatternParser extends PatternParser {
	private static final char SIMULATION_TIME_CHAR = 's';
	
	public SimulationPatternParser(String pattern) {
		super(pattern);
	}
	
	@Override
	protected void finalizeConverter(char c) {
		switch (c) {
		case SIMULATION_TIME_CHAR:
			currentLiteral.setLength(0);
			addConverter(new SimulationPatternConverter(this.formattingInfo));
			break;
		default:
			super.finalizeConverter(c);
		}
	}
} 
