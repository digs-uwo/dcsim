package edu.uwo.csd.dcsim.examples;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.*;

import edu.uwo.csd.dcsim.*;
import edu.uwo.csd.dcsim.common.SimTime;
import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.examples.management.ConsolidationPolicy;
import edu.uwo.csd.dcsim.examples.management.RelocationPolicy;
import edu.uwo.csd.dcsim.management.*;
import edu.uwo.csd.dcsim.vm.*;

public class DynamicManagement extends SimulationTask {

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
			task.getMetrics().printDefault(logger);
		}

	}
	
	public DynamicManagement(String name, long randomSeed) {
		super(name, SimTime.days(10));
		this.setMetricRecordStart(SimTime.days(0));
		this.setRandomSeed(randomSeed);
	}

	@Override
	public void setup(Simulation simulation) {
		
		AutonomicManager dcAM = ExampleHelper.createDataCentre(simulation);
		
		ArrayList<VmAllocationRequest> vmList = ExampleHelper.createVmList(simulation, false);
				
		ExampleHelper.placeVms(vmList, dcAM, simulation);
		
		dcAM.installPolicy(new RelocationPolicy(0.5, 0.9, 0.85), SimTime.hours(1), SimTime.hours(1) + 1);
		dcAM.installPolicy(new ConsolidationPolicy(0.5, 0.9, 0.85), SimTime.hours(2), SimTime.hours(2) + 2);
	}
	
}
