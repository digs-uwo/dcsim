package edu.uwo.csd.dcsim2.host;

import java.util.Vector;

import edu.uwo.csd.dcsim2.vm.*;

public abstract class CpuScheduler {

	private Host host;
	private CpuSchedulerState state;
	private Vector<Cpu> availableCpuCapacity;
	
	public enum CpuSchedulerState {READY, COMPLETE;}
	
	public CpuScheduler() {
		MasterCpuScheduler.getMasterCpuScheduler().getCpuSchedulers().add(this);
		state = CpuSchedulerState.READY;
	}
	
	public void prepareScheduler() {
		state = CpuSchedulerState.READY;
		//TODO: calculate and set availableCpuCapacity
	}
	
	public abstract void beginScheduling();
	public abstract void beginRound();
	public abstract boolean processVM(VMAllocation vmAllocation);
	public abstract void endRound();
	public abstract void endScheduling();
	
	protected Vector<Cpu> getAvailableCpuCapacity() {
		return availableCpuCapacity;
	}
	
	protected void setAvailableCpuCapacity(Vector<Cpu> availableCpuCapacity) {
		this.availableCpuCapacity = availableCpuCapacity; 
	}
	
	public CpuSchedulerState getState() {
		return state;
	}
	
	public void setCpuSchedulerState(CpuSchedulerState state) {
		this.state = state;
	}
	
	public void setHost(Host host) {
		this.host = host;
	}
	
	public Host getHost() {
		return host;
	}
	
}
