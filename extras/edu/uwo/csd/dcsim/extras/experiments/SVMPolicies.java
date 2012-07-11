/**
 * 
 */
package edu.uwo.csd.dcsim.extras.experiments;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.*;

import edu.uwo.csd.dcsim.*;
import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.examples.ExampleHelper;
import edu.uwo.csd.dcsim.extras.policies.*;
import edu.uwo.csd.dcsim.management.*;
import edu.uwo.csd.dcsim.vm.*;

/**
 * @author gurtaj
 *
 */
public class SVMPolicies extends DCSimulationTask {

	private static Logger logger = Logger.getLogger(SVMPolicies.class);
	
	public static void main(String args[]) {
		
		Simulation.initializeLogging();
		
		Collection<SimulationTask> completedTasks;
		SimulationExecutor executor = new SimulationExecutor();
		
		executor.addTask(new SVMPolicies("dynamic-1", 6198910678692541341l));
//		executor.addTask(new SVMPolicies("dynamic-2", 5646441053220106016l));
//		executor.addTask(new SVMPolicies("dynamic-3", -5705302823151233610l));
//		executor.addTask(new SVMPolicies("dynamic-4", 8289672009575825404l));
//		executor.addTask(new SVMPolicies("dynamic-5", -4637549055860880177l));
		
		completedTasks = executor.execute();
		
		for(SimulationTask task : completedTasks) {
			logger.info(task.getName());
			ExampleHelper.printMetrics(task.getResults());
		}

	}

	public SVMPolicies(String name, long randomSeed) {
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
		 * Relocation & Consolidation policies.
		 */
		//VMRelocationPolicyFFDI vmRelocationPolicy = new VMRelocationPolicyFFDI(simulation, dc, 600000, 0.5, 0.85, 0.85);
		//VMRelocationPolicyFFDD vmRelocationPolicy = new VMRelocationPolicyFFDD(simulation, dc, 600000, 0.5, 0.85, 0.85);
		//VMRelocationPolicyFFDM vmRelocationPolicy = new VMRelocationPolicyFFDM(simulation, dc, 600000, 0.5, 0.85, 0.85);
		//VMRelocationPolicyFFII vmRelocationPolicy = new VMRelocationPolicyFFII(simulation, dc, 600000, 0.5, 0.85, 0.85);
		//VMRelocationPolicyFFID vmRelocationPolicy = new VMRelocationPolicyFFID(simulation, dc, 600000, 0.5, 0.85, 0.85);
		VMRelocationPolicyFFIM vmRelocationPolicy = new VMRelocationPolicyFFIM(simulation, dc, 600000, 0.5, 0.85, 0.85);
		vmRelocationPolicy.start(600000);
		
		VMConsolidationPolicySimple vmConsolidationPolicy = new VMConsolidationPolicySimple(simulation, dc, 14400000, 0.5, 0.85);
		vmConsolidationPolicy.start(14401000);
		//vmConsolidationPolicy.start(86401000);
		//vmConsolidationPolicy.start(3601000);
		
		//VMAllocationPolicyGreedy vmAllocationPolicy = new VMAllocationPolicyGreedy(simulation, dc, 600000, 0.5, 0.85, 0.85);
		//vmAllocationPolicy.start(600000);
	}

}
