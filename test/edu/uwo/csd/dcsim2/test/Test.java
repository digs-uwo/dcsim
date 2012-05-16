package edu.uwo.csd.dcsim2.test;

import java.util.Collection;

import org.apache.log4j.Logger;

import edu.uwo.csd.dcsim2.*;
import edu.uwo.csd.dcsim2.common.Utility;
import edu.uwo.csd.dcsim2.core.Simulation;
import edu.uwo.csd.dcsim2.core.metrics.Metric;

public class Test {

	private static Logger logger = Logger.getLogger(Test.class);
	
	public static void main(String args[]) {
		Simulation.initializeLogging();

		SimulationExecutor executor = new SimulationExecutor();
		
		executor.addTask(new DynamicManagement("test1", 1088501048448116498l));
		executor.addTask(new StaticPeak("test2", 1088501048448116498l));
		executor.addTask(new StaticAverage("test3", 1088501048448116498l));
		executor.addTask(new Replication("test4", 1088501048448116498l));
		
		Collection<SimulationTask> completedTasks;
		completedTasks = executor.execute();
		
		for (SimulationTask test : completedTasks) {
			if (test.getName() == "test1") {
				for (Metric metric : test.getResults()) {
					
					if (metric.getName().equals("hostTime")) {
						if (metric.getValue() != 2.8649842E7) {
							logger.error("test1 failed on hostTime metric");
							return;
						}
					}
					if (metric.getName().equals("slaViolation")) {
						if (Utility.roundDouble(metric.getValue(), 6) != 0.008089) {
							logger.error("test1 failed on slaViolation metric");
							return;
						}
					}
					if (metric.getName().equals("avgActiveHosts")) {
						if (metric.getValue() != 36.84645061728395) {
							logger.error("test1 failed on avgActiveHosts metric");
							return;
						}
					}
					if (metric.getName().equals("powerConsumed")) {
						if (metric.getValue() != 2.7759756737429924E10) {
							logger.error("test1 failed on powerConsumed metric");
							return;
						}
					}
					if (metric.getName().equals("migrationCount")) {
						if (metric.getValue() != 17842.0) {
							logger.error("test1 failed on migrationCount metric");
							return;
						}
					}
							
				}
			} else if(test.getName() == "test2") {
					
				for (Metric metric : test.getResults()) {
					if (metric.getName().equals("hostTime")) {
						if (metric.getValue() != 4.51008E7) {
							logger.error("test2 failed on hostTime metric");
							return;
						}
					}
					if (metric.getName().equals("slaViolation")) {
						if (Utility.roundDouble(metric.getValue(), 6) != 0.0) {
							logger.error("test2 failed on slaViolation metric");
							return;
						}
					}
					if (metric.getName().equals("avgActiveHosts")) {
						if (metric.getValue() != 58.0) {
							logger.error("test2 failed on avgActiveHosts metric");
							return;
						}
					}
					if (metric.getName().equals("powerConsumed")) {
						if (metric.getValue() != 2.7619356175729847E10) {
							logger.error("test2 failed on powerConsumed metric");
							return;
						}
					}
			
				}
				
			} else if (test.getName() == "test3") {

				for (Metric metric : test.getResults()) {
					if (metric.getName().equals("hostTime")) {
						if (metric.getValue() != 1.944E7) {
							logger.error("test3 failed on hostTime metric");
							return;
						}
					}
					if (metric.getName().equals("slaViolation")) {
						if (Utility.roundDouble(metric.getValue(), 5) != 0.21480) {
							logger.error("test3 failed on slaViolation metric");
							return;
						}
					}
					if (metric.getName().equals("avgActiveHosts")) {
						if (metric.getValue() != 25.0) {
							logger.error("test3 failed on avgActiveHosts metric");
							return;
						}
					}
					if (metric.getName().equals("powerConsumed")) {
						if (metric.getValue() != 2.7354329346395138E10) {
							logger.error("test3 failed on powerConsumed metric");
							return;
						}
					}
			
				}
			
			} else if (test.getName() == "test4") {

				for (Metric metric : test.getResults()) {
					
					if (metric.getName().equals("hostTime")) {
						if (metric.getValue() != 3.650151E7) {
							logger.error("test1 failed on hostTime metric");
							return;
						}
					}
					if (metric.getName().equals("slaViolation")) {
						if (Utility.roundDouble(metric.getValue(), 6) != 0.012247) {
							logger.error("test1 failed on slaViolation metric");
							return;
						}
					}
					if (metric.getName().equals("avgActiveHosts")) {
						if (metric.getValue() != 46.97357098955132) {
							logger.error("test1 failed on avgActiveHosts metric");
							return;
						}
					}
					if (metric.getName().equals("powerConsumed")) {
						if (metric.getValue() != 1.5586989286676231E10) {
							logger.error("test1 failed on powerConsumed metric");
							return;
						}
					}
					if (metric.getName().equals("replicationCount")) {
						if (metric.getValue() != 18861.0) {
							logger.error("test1 failed on replicationCount metric");
							return;
						}
					}
					if (metric.getName().equals("shutdownCount")) {
						if (metric.getValue() != 18853.0) {
							logger.error("test1 failed on shutdownCount metric");
							return;
						}
					}
							
				}
			} else {
				logger.error("unknown test name");
				return;
			}
		}
		
		logger.info("Passed");

		
	}
	
}
