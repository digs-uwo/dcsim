package edu.uwo.csd.dcsim2.logging;

import org.apache.log4j.helpers.PatternConverter; 
import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.spi.LoggingEvent;

import edu.uwo.csd.dcsim2.core.*;

public class SimulationPatternConverter extends PatternConverter {
	
	public SimulationPatternConverter(FormattingInfo formattingInfo) {
		super(formattingInfo);
	}
	
	@Override
    protected String convert(LoggingEvent evt) { 
        return Long.toString((Simulation.getSimulation().getSimulationTime())); 
    } 
	
}
