package edu.uwo.csd.dcsim2.host.scheduler;

import java.util.ArrayList;

import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.host.Cpu;
import edu.uwo.csd.dcsim2.host.Host;
import edu.uwo.csd.dcsim2.vm.*;

public abstract class CpuScheduler {

	private Host host;
	private CpuSchedulerState state;
	private ArrayList<Cpu> availableCpuCapacity;
	
	public enum CpuSchedulerState {READY, COMPLETE;}
	
	public CpuScheduler() {
		MasterCpuScheduler.getMasterCpuScheduler().getCpuSchedulers().add(this);
		state = CpuSchedulerState.READY;
	}
	
	public void prepareScheduler() {
		state = CpuSchedulerState.READY;
		
		long elapsedTime = Simulation.getSimulation().getSimulationTime() - Simulation.getSimulation().getLastUpdate();
		
		availableCpuCapacity = new ArrayList<Cpu>();
		for (Cpu cpu : host.getCpus()) {
			availableCpuCapacity.add(new Cpu(cpu.getCores(), (int)((cpu.getCoreCapacity() / 1000) * elapsedTime))); //core capacity in shares/second, elapsed time in ms
		}
		
	}
	
	public abstract void beginScheduling();
	public abstract void beginRound();
	public abstract boolean processVM(VMAllocation vmAllocation);
	public abstract void endRound();
	public abstract void endScheduling();
	public abstract void completeRemainingScheduling();
	
	protected ArrayList<Cpu> getAvailableCpuCapacity() {
		return availableCpuCapacity;
	}
	
	protected void setAvailableCpuCapacity(ArrayList<Cpu> availableCpuCapacity) {
		this.availableCpuCapacity = availableCpuCapacity; 
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
