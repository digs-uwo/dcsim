package edu.uwo.csd.dcsim.examples;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.*;

import edu.uwo.csd.dcsim.*;
import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.management.*;
import edu.uwo.csd.dcsim.vm.*;

public class DynamicManagement extends DCSimulationTask {

	private static Logger logger = Logger.getLogger(DynamicManagement.class);
	
	public static void main(String args[]) {
		
		Simulation.initializeLogging();
		
		Collection<SimulationTask> completedTasks;
		SimulationExecutor executor = new SimulationExecutor();
		
		executor.addTask(new DynamicManagement("dynamic-1", 1088501048448116498l));
//		executor.addTask(new DynamicManagement("dynamic-2", 3081198553457496232l));
//		executor.addTask(new DynamicManagement("dynamic-3", -2485691440833440205l));
//		executor.addTask(new DynamicManagement("dynamic-4", 2074739686644571611l));
//		executor.addTask(new DynamicManagement("dynamic-5", -1519296228623429147l));
		
		completedTasks = executor.execute();
		
		for(SimulationTask task : completedTasks) {
			logger.info(task.getName());
			ExampleHelper.printMetrics(task.getResults());
		}

	}
	
	public DynamicManagement(String name, long randomSeed) {
		super(name, 864000000);
		this.setMetricRecordStart(86400000);
		this.setRandomSeed(randomSeed);
	}

	@Override
	public void setup(DataCentreSimulation simulation) {
		
		DataCentre dc = ExampleHelper.createDataCentre(simulation);
		simulation.addDatacentre(dc);
		
		ArrayList<VMAllocationRequest> vmList = ExampleHelper.createVmList(simulation, false);
				
		ExampleHelper.placeVms(vmList, dc);
		
		/*
		 * Basic Greedy Relocation & Consolidation together. Relocation same as RelocST03, Consolidation similar but
		 * evicts ALL VMs from underprovisioned hosts, not 1.
		 */
		VMAllocationPolicyGreedy vmAllocationPolicyGreedy = new VMAllocationPolicyGreedy(dc, 0.5, 0.85, 0.85);
		DaemonScheduler daemon = new FixedIntervalDaemonScheduler(simulation, 600000, vmAllocationPolicyGreedy);
		daemon.start(600000);
	}
	
}
