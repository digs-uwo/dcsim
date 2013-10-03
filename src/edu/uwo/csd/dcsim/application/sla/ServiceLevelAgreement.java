package edu.uwo.csd.dcsim.application.sla;

public interface ServiceLevelAgreement {

	public boolean evaluate();
	public double calculatePenalty();
	
}
