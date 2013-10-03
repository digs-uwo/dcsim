package edu.uwo.csd.dcsim.application;

import java.util.ArrayList;

import edu.uwo.csd.dcsim.host.Resources;

/**
 * @author Michael Tighe
 *
 */
public class VmmTask extends Task {

	private VmmApplication application;
	private VmmTaskInstance instance;
	
	public VmmTask(VmmApplication application, int defaultInstances, int minInstances, int maxInstances,
			Resources resourceSize) {
		super(defaultInstances, resourceSize);
		
		this.application = application;
		instance = new VmmTaskInstance(this);
	}

	@Override
	public TaskInstance createInstance() {
		return instance;
	}

	@Override
	public void removeInstance(TaskInstance instance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startInstance(TaskInstance instance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopInstance(TaskInstance instance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<TaskInstance> getInstances() {
		ArrayList<TaskInstance> instances = new ArrayList<TaskInstance>();
		instances.add(instance);
		return instances;
	}

	@Override
	public Application getApplication() {
		return application;
	}

	public VmmTaskInstance getInstance() {
		return instance;
	}


}
