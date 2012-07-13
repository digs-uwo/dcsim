package edu.uwo.csd.dcsim.extras.experiments;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.*;

import edu.uwo.csd.dcsim.*;
import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.extras.policies.*;
import edu.uwo.csd.dcsim.management.*;
import edu.uwo.csd.dcsim.vm.*;

/**
 * This class defines experiments _similar_ to those run for the SVM 2012 
 * paper. These experiments are not exactly the same as those of the SVM paper 
 * due to several modifications that took place in DCSim since the submission 
 * of said paper.
 *   
 * @author Gaston Keller
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
			DataCentreTestEnvironment.printMetrics(task.getResults());
		}

	}

	public SVMPolicies(String name, long randomSeed) {
		super(name, 864000000);
		this.setMetricRecordStart(86400000);
		this.setRandomSeed(randomSeed);
	}

	@Override
	public void setup(DataCentreSimulation simulation) {
		DataCentre dc = DataCentreTestEnvironment.createDataCentre(simulation);
		simulation.addDatacentre(dc);
		ArrayList<VMAllocationRequest> vmList = DataCentreTestEnvironment.createVmList(simulation, true);
		DataCentreTestEnvironment.placeVms(vmList, dc);
		
		DCUtilizationMonitor dcMon = new DCUtilizationMonitor(simulation, 120000, 5, dc);
		simulation.addMonitor(dcMon);
		
		/*
		 * Relocation policies.
		 */
		//VMRelocationPolicyFFDI vmRelocationPolicy = new VMRelocationPolicyFFDI(dc, dcMon, 0.5, 0.85, 0.85);
		//VMRelocationPolicyFFDD vmRelocationPolicy = new VMRelocationPolicyFFDD(dc, dcMon, 0.5, 0.85, 0.85);
		//VMRelocationPolicyFFDM vmRelocationPolicy = new VMRelocationPolicyFFDM(dc, dcMon, 0.5, 0.85, 0.85);
		//VMRelocationPolicyFFII vmRelocationPolicy = new VMRelocationPolicyFFII(dc, dcMon, 0.5, 0.85, 0.85);
		VMRelocationPolicyFFID vmRelocationPolicy = new VMRelocationPolicyFFID(dc, dcMon, 0.5, 0.85, 0.85);
		//VMRelocationPolicyFFIM vmRelocationPolicy = new VMRelocationPolicyFFIM(dc, dcMon, 0.5, 0.85, 0.85);
		
		DaemonScheduler relocationPolicyDaemon = new FixedIntervalDaemonScheduler(simulation, 600000, vmRelocationPolicy);
		relocationPolicyDaemon.start(600000);
		
		/*
		 * Consolidation policies.
		 */
		VMConsolidationPolicySimple vmConsolidationPolicy = new VMConsolidationPolicySimple(dc, dcMon, 0.5, 0.85);
		
		DaemonScheduler consolidationPolicyDaemon = new FixedIntervalDaemonScheduler(simulation, 14400000, vmConsolidationPolicy);
		consolidationPolicyDaemon.start(14401000);
		//consolidationPolicyDaemon.start(86401000);
		//consolidationPolicyDaemon.start(3601000);
		
		/*
		 * Relocation + Consolidation policies.
		 */
		//VMAllocationPolicyGreedy vmAllocationPolicy = new VMAllocationPolicyGreedy(simulation, dc, 600000, 0.5, 0.85, 0.85);
		//vmAllocationPolicy.start(600000);
	}

}
