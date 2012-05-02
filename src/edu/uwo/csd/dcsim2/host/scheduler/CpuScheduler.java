package edu.uwo.csd.dcsim2.host.scheduler;

import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.core.Utility;
import edu.uwo.csd.dcsim2.host.Host;
import edu.uwo.csd.dcsim2.vm.*;

public abstract class CpuScheduler {

	private Host host;
	private CpuSchedulerState state;
	private double availableCpu;
	protected Simulation simulation;
	
	public enum CpuSchedulerState {READY, COMPLETE;}
	
	public CpuScheduler(Simulation simulation) {
		this.simulation = simulation;
		
		MasterCpuScheduler.getMasterCpuScheduler().getCpuSchedulers().add(this);
		state = CpuSchedulerState.READY;
	}
	
	public void prepareScheduler() {
		state = CpuSchedulerState.READY;
		
		availableCpu = host.getTotalCpu() * (simulation.getElapsedSeconds()); //cpu in shares/second, elapsed time in ms

	}
	
	public abstract void schedulePrivDomain(VMAllocation privDomainAllocation);
	public abstract void beginScheduling();
	public abstract void beginRound();
	public abstract boolean processVM(VMAllocation vmAllocation);
	public abstract void endRound();
	public abstract void endScheduling();
	
	protected double getAvailableCpu() {
		return availableCpu;
	}
	
	protected void consumeAvailableCpu(double cpuConsumed) {
		availableCpu -= cpuConsumed;
		availableCpu = Utility.roundDouble(availableCpu); //round off precision errors
		
		if (getAvailableCpu() <= 0) {
			this.setState(CpuSchedulerState.COMPLETE);
		}
		if (getAvailableCpu() < 0)
			throw new RuntimeException("CPU Scheduler on Host #" + getHost().getId() + " used more CPU than available (sim time: " + simulation.getSimulationTime() + ")");
	}
	
	public CpuSchedulerState getState() {
		return state;
	}
	
	public void setState(CpuSchedulerState state) {
		this.state = state;
	}
	
	public void setHost(Host host) {
		this.host = host;
	}
	
	public Host getHost() {
		return host;
	}
	
}
