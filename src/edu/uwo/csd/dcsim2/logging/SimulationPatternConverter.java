package edu.uwo.csd.dcsim2.logging;

import org.apache.log4j.helpers.PatternConverter; 
import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.spi.LoggingEvent;

import edu.uwo.csd.dcsim2.core.Simulation;

public class SimulationPatternConverter extends PatternConverter {
	
	Simulation simulation;
	
	public SimulationPatternConverter(Simulation simulation, FormattingInfo formattingInfo) {
		super(formattingInfo);
		this.simulation = simulation;
	}
	
	@Override
    protected String convert(LoggingEvent evt) { 
        return Long.toString((simulation.getSimulationTime()));
    } 
	
}
