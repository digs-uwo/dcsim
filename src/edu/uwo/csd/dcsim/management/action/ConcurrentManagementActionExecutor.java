package edu.uwo.csd.dcsim.management.action;

import java.util.*;

import edu.uwo.csd.dcsim.core.Simulation;

public class ConcurrentManagementActionExecutor extends ManagementAction {

	private Collection<ManagementAction> actions = new ArrayList<ManagementAction>();
	private int completed = 0;
	
	public ConcurrentManagementActionExecutor() {
		
	}
	
	public ConcurrentManagementActionExecutor(Collection<ManagementAction> actions) {
		this.actions.addAll(actions);
	}
	
	public void addAction(ManagementAction action) {
		actions.add(action);
	}
	
	@Override
	public void execute(Simulation simulation, Object triggeringEntity) {
		for (ManagementAction action : actions) {
			action.setParentAction(this);
			action.execute(simulation, triggeringEntity);
		}
	}
	
	@Override
	public void subActionCompleted(ManagementAction action) {
		++completed;
		if (completed == actions.size()) {
			completeAction();
		}
	}

}
