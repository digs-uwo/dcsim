package edu.uwo.csd.dcsim.application.sla;

import edu.uwo.csd.dcsim.application.InteractiveApplication;

public class InteractiveServiceLevelAgreement implements ServiceLevelAgreement {

	InteractiveApplication application;
	double responseTime;
	double throughput;
	double responseTimePenalty = 0;
	double throughputPenalty = 0;
	
	public InteractiveServiceLevelAgreement(InteractiveApplication application) {
		this.application = application;
		this.responseTime  = Float.MAX_VALUE;
		this.throughput = Float.MAX_VALUE;
	}
	
	//Usage: InteractiveServiceLevelAgreement i = new InteractiveServiceLevelAgreement(application).responseTime(1f).throughput(2.5f);
	public InteractiveServiceLevelAgreement responseTime(double responseTime) {
		this.responseTime = responseTime;
		return this;
	}
	
	public InteractiveServiceLevelAgreement responseTime(double responseTime, double penaltyRate) {
		this.responseTime = responseTime;
		this.responseTimePenalty = penaltyRate;
		return this;
	}
	
	public InteractiveServiceLevelAgreement throughput(double throughput) {
		this.throughput = throughput;
		return this;
	}
	
	public InteractiveServiceLevelAgreement throughput(double throughput, double penaltyRate) {
		this.throughput = throughput;
		this.throughputPenalty = penaltyRate;
		return this;
	}
	
	public double getResponseTime() {
		return responseTime;
	}
	
	public double getThroughput() {
		return throughput;
	}
	
	@Override
	public boolean evaluate() {

		if (application.getResponseTime() > responseTime) return false;
		
		if (application.getThroughput() > throughput) return false;

		return true;
	}

	@Override
	public double calculatePenalty() {
		double penalty = 0;
		
		if (application.getResponseTime() > responseTime) {
			penalty += responseTimePenalty;
		}
		
		if (application.getThroughput() > throughput) {
			penalty += throughputPenalty;
		}
		
		return penalty;
	}
	
}
