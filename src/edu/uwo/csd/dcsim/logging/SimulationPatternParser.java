package edu.uwo.csd.dcsim.logging;

import org.apache.log4j.helpers.PatternParser;

import edu.uwo.csd.dcsim.core.Simulation;

public class SimulationPatternParser extends PatternParser {
	private static final char SIMULATION_TIME_CHAR = 's';
	
	private Simulation simulation;
	
	public SimulationPatternParser(Simulation simulation, String pattern) {
		super(pattern);
		this.simulation = simulation;
	}
	
	@Override
	protected void finalizeConverter(char c) {
		switch (c) {
		case SIMULATION_TIME_CHAR:
			currentLiteral.setLength(0);
			addConverter(new SimulationPatternConverter(simulation, this.formattingInfo));
			break;
		default:
			super.finalizeConverter(c);
		}
	}
} 
