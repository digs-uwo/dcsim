package edu.uwo.csd.dcsim2.host.scheduler;

import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.host.Cpu;
import edu.uwo.csd.dcsim2.host.Host;
import edu.uwo.csd.dcsim2.vm.*;

public abstract class CpuScheduler {

	private Host host;
	private CpuSchedulerState state;
	protected double availableCpu;
	
	public enum CpuSchedulerState {READY, COMPLETE;}
	
	public CpuScheduler() {
		MasterCpuScheduler.getMasterCpuScheduler().getCpuSchedulers().add(this);
		state = CpuSchedulerState.READY;
	}
	
	public void prepareScheduler() {
		state = CpuSchedulerState.READY;
		
		long elapsedTime = Simulation.getSimulation().getSimulationTime() - Simulation.getSimulation().getLastUpdate();
		
		availableCpu = 0;
		for (Cpu cpu : host.getCpus()) {
			availableCpu += cpu.getCores() * cpu.getCoreCapacity() * (elapsedTime / 1000.0); //core capacity in shares/second, elapsed time in ms
		}
		
	}
	
	public abstract void beginScheduling();
	public abstract void beginRound();
	public abstract boolean processVM(VMAllocation vmAllocation);
	public abstract void endRound();
	public abstract void endScheduling();
	public abstract void completeRemainingScheduling();
	
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
