package edu.uwo.csd.dcsim.management.action;

import java.util.Collection;
import java.util.LinkedList;

import edu.uwo.csd.dcsim.core.Simulation;

public class SequentialManagementActionExecutor extends ManagementAction {

	private LinkedList<ManagementAction> actionQueue = new LinkedList<ManagementAction>();
	private Simulation simulation;
	private Object triggeringEntity;
	
	public SequentialManagementActionExecutor() {
		
	}
	
	public SequentialManagementActionExecutor(ManagementAction action) {
		addAction(action);
	}
	
	public SequentialManagementActionExecutor(Collection<? extends ManagementAction> actions) {
		addActions(actions);
	}
	
	public void addAction(ManagementAction action) {
		actionQueue.add(action);
	}
	
	public void addActions(Collection<? extends ManagementAction> actions) {
		actionQueue.addAll(actions);
	}
	
	@Override
	public void execute(Simulation simulation, Object triggeringEntity) {
		this.simulation = simulation;
		this.triggeringEntity = triggeringEntity;
		
		//if there is an action left on the queue, run it
		if (!actionQueue.isEmpty()) {
			ManagementAction action = actionQueue.pop();
			action.setParentAction(this);
			action.execute(simulation, triggeringEntity);
		}
	}
	
	public void subActionCompleted(ManagementAction action) {
		//execute next action
		execute(simulation, triggeringEntity);
	}
	

}
