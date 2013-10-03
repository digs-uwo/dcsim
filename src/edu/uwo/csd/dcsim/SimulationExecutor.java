package edu.uwo.csd.dcsim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

import org.apache.log4j.Logger;

/**
 * Executes a Collection of SimulationTasks concurrently. 
 * 
 * @author Michael Tighe
 *
 */
public class SimulationExecutor {

	private static Logger logger = Logger.getLogger(SimulationExecutor.class);
	
	private ArrayList<SimulationTask> tasks = new ArrayList<SimulationTask>(); //the tasks to execute
	
	/**
	 * Add a task to the Collection of tasks to execute
	 * @param task
	 */
	public void addTask(SimulationTask task) {
		tasks.add(task);
	}
	
	/**
	 * Add a collection of tasks to execute
	 * @param tasks
	 */
	public void addTasks(Collection<SimulationTask> taskCollection) {
		tasks.addAll(taskCollection);
	}
	
	/**
	 * Execute the tasks, in parallel. This method blocks until all tasks have completed.
	 * 
	 * @return
	 */
	public List<SimulationTask> execute() {
		return execute(Executors.newCachedThreadPool());
	}
	
	public List<SimulationTask> execute(int nThreads) {
		return execute(Executors.newFixedThreadPool(nThreads));
	}
	
	private List<SimulationTask> execute(ExecutorService executorService) {
		
		long startTime = System.currentTimeMillis();
		
		//create callable objects from simulation tasks (required for ExecutorService)
		Collection<Callable<Object>> callableTasks = new ArrayList<Callable<Object>>();
		for (SimulationTask task : tasks)
			callableTasks.add(Executors.callable(task));
		
		//run the tasks
		try {
			executorService.invokeAll(callableTasks);
		} catch (InterruptedException e) {
			throw new RuntimeException("Could not complete simulation task execution", e);
		}
		
		executorService.shutdown(); //shutdown all threads (otherwise they will be cached to await further tasks)
		
		long endTime = System.currentTimeMillis();
		
		//report on the total real time it took to execute all tasks
		logger.info("Executed Simulation Tasks in " + ((endTime - startTime) / 1000) + "s");

		return tasks;
	}
	
}
