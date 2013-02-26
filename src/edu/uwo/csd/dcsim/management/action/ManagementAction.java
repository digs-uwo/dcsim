package edu.uwo.csd.dcsim.management.action;

import edu.uwo.csd.dcsim.core.*;

public abstract class ManagementAction {

	private ManagementAction parentAction = null;
	
	public abstract void execute(Simulation simulation, Object triggeringEntity);
	
	protected final void completeAction() {
		if (parentAction != null) {
			parentAction.subActionCompleted(this);
		}
	}
	
	public final void setParentAction(ManagementAction parent) {
		this.parentAction = parent;
	}
	
	public void subActionCompleted(ManagementAction action) {
		//does nothing by default, indicating a 'leaf' action, not a composite
	}
	
}
