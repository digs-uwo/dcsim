package edu.uwo.csd.dcsim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.*;

import org.apache.log4j.Logger;

public class SimulationExecutor {

	private static Logger logger = Logger.getLogger(SimulationExecutor.class);
	
	private ArrayList<SimulationTask> tasks = new ArrayList<SimulationTask>();
	
	public void addTask(SimulationTask task) {
		tasks.add(task);
	}
	
	public Collection<SimulationTask> execute() {
		
		long startTime = System.currentTimeMillis();
		
		//create callable objects from simulation tasks
		Collection<Callable<Object>> callableTasks = new ArrayList<Callable<Object>>();
		for (SimulationTask task : tasks)
			callableTasks.add(Executors.callable(task));
		
		//run the tasks
		ExecutorService executorService = Executors.newCachedThreadPool();
		try {
			executorService.invokeAll(callableTasks);
		} catch (InterruptedException e) {
			throw new RuntimeException("Could not complete simulation task execution", e);
		}
		
		executorService.shutdown();
		
		long endTime = System.currentTimeMillis();
		
		logger.info("Executed Simulation Tasks in " + ((endTime - startTime) / 1000) + "s");

		return tasks;
	}
	
}
