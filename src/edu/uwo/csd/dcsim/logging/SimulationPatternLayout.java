package edu.uwo.csd.dcsim.logging;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.PatternParser;

import edu.uwo.csd.dcsim.core.Simulation;

public class SimulationPatternLayout extends PatternLayout {

	private Simulation simulation;
	
	public SimulationPatternLayout(Simulation simulation) {
		super();
		this.simulation = simulation;
	}
	
	@Override
	protected PatternParser createPatternParser(String pattern) {
		return new SimulationPatternParser(simulation, pattern);
	}
		
}
