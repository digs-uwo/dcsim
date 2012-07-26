package edu.uwo.csd.dcsim.core;

import java.util.*;

public class DaemonSchedulerGroup implements DaemonScheduler {

	protected Simulation simulation;
	boolean running = false;
	boolean enabled = true;
	public List<DaemonGroupMember> members = new ArrayList<DaemonGroupMember>();
	
	public DaemonSchedulerGroup(Simulation simulation) {
		this.simulation = simulation;
	}
	
	public void addDaemon(DaemonScheduler daemonScheduler, long startOffset) {
		members.add(new DaemonGroupMember(daemonScheduler, startOffset));
	}
	
	@Override
	public void start() {
		start(simulation.getSimulationTime());
	}

	@Override
	public void start(long time) {
		for (DaemonGroupMember member : members) {
			member.daemonScheduler.start(time + member.startOffset);
		}
		running = true;
	}

	@Override
	public void stop() {
		for (DaemonGroupMember member : members) {
			member.daemonScheduler.stop();
		}
		running = false;
	}

	@Override
	public boolean isRunning() {
		return running;
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		for (DaemonGroupMember member : members) {
			member.daemonScheduler.setEnabled(enabled);
		}
	}
	
	private class DaemonGroupMember {
		private final DaemonScheduler daemonScheduler;
		private final long startOffset;
		
		public DaemonGroupMember(DaemonScheduler daemonScheduler, long startOffset) {
			this.daemonScheduler = daemonScheduler;
			this.startOffset = startOffset;
		}
		
	}

	
}
