package edu.uwo.csd.dcsim2.host.scheduler;

import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.core.Utility;
import edu.uwo.csd.dcsim2.host.Cpu;
import edu.uwo.csd.dcsim2.host.Host;
import edu.uwo.csd.dcsim2.vm.*;

public abstract class CpuScheduler {

	private Host host;
	private CpuSchedulerState state;
	private double availableCpu;
	
	public enum CpuSchedulerState {READY, COMPLETE;}
	
	public CpuScheduler() {
		MasterCpuScheduler.getMasterCpuScheduler().getCpuSchedulers().add(this);
		state = CpuSchedulerState.READY;
	}
	
	public void prepareScheduler() {
		state = CpuSchedulerState.READY;
		
		long elapsedTime = Simulation.getInstance().getSimulationTime() - Simulation.getInstance().getLastUpdate();
		
		availableCpu = 0;
		for (Cpu cpu : host.getCpus()) {
			availableCpu += cpu.getCores() * cpu.getCoreCapacity() * (elapsedTime / 1000.0); //core capacity in shares/second, elapsed time in ms
			availableCpu = Utility.roundDouble(availableCpu); //round off precision errors
		}
		
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
