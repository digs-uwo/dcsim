package edu.uwo.csd.dcsim2.host;

import java.util.*;

public class MasterCpuScheduler {

	private static MasterCpuScheduler masterCpuScheduler;
	
	private Vector<CpuScheduler> cpuSchedulers;
	
	public static MasterCpuScheduler getMasterCpuScheduler() {
		if (masterCpuScheduler == null) {
			masterCpuScheduler = new MasterCpuScheduler();
		}
		return masterCpuScheduler;
	}
	
	private MasterCpuScheduler() {
		cpuSchedulers = new Vector<CpuScheduler>();	
	}
	
	public Vector<CpuScheduler> getCpuSchedulers() {
		return cpuSchedulers;
	}
	
	public void scheduleCpu() {
		//TODO order VMs, schedule CPU
		//check that host is powered on
	}
}
