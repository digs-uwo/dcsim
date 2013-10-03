package edu.uwo.csd.dcsim.application;

import java.util.ArrayList;

import edu.uwo.csd.dcsim.core.Simulation;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.host.Resources;

public class VmmApplication extends Application {

	private Host host;
	private int cpuOverhead;
	private int migOverhead;
	private VmmTask vmmTask;
	private VmmTaskInstance vmmTaskInstance;
	private int cpuDemand;
	
	public VmmApplication(Simulation simulation, Host host,
			int cpu, int memory, int bandwidth, int storage) {
		super(simulation);
		
		cpuOverhead = Integer.parseInt(Simulation.getProperty("vmmCpuOverhead"));
		migOverhead = Integer.parseInt(Simulation.getProperty("vmMigrationCpuOverhead"));
		
		this.host = host;
		
		//create VmmTask
		VmmTask task = new VmmTask(this, 1, 1, 1, new Resources(cpu, memory, bandwidth, storage));
		vmmTask = task;
		vmmTaskInstance = vmmTask.getInstance();
		
	}

	@Override
	public void initializeScheduling() {
		Resources resourcesDemand = new Resources();
		resourcesDemand.setMemory(0);
		resourcesDemand.setBandwidth(0);
		resourcesDemand.setStorage(0);
		resourcesDemand.setCpu(cpuOverhead + migOverhead * (host.getMigratingIn().size() + host.getMigratingOut().size()));
		
		vmmTaskInstance.setResourceDemand(resourcesDemand);
		vmmTaskInstance.setFullDemand(resourcesDemand);
		
		cpuDemand = resourcesDemand.getCpu();
	}

	@Override
	public boolean updateDemand() {
		//VMM resource demand is independent of all other task resource demands and therefore needs only to be calculated once 		
		
		return false;
	}

	@Override
	public void postScheduling() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void advanceSimulation() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int getTotalCpuDemand() {
		return cpuDemand;
	}

	@Override
	public int getTotalCpuScheduled() {
		return cpuDemand; //scheduled is the same as demand, as we assume the Vmm always gets full demand
	}
	
	public VmmTask getVmmTask() {
		return vmmTask;
	}
	
	public VmmTaskInstance getVmmTaskInstance() {
		return vmmTaskInstance;
	}

	@Override
	public ArrayList<Task> getTasks() {
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.add(vmmTask);
		return tasks;
	}



}
