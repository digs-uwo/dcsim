package edu.uwo.csd.dcsim.management;

import java.util.ArrayList;
import java.util.List;

import edu.uwo.csd.dcsim.core.*;

public abstract class AutonomicManager implements SimulationEventListener {

	private ArrayList<Policy> policies = new ArrayList<Policy>();
	
	public List<Policy> getPolicies() {
		return policies;
	}
	
	public void installPolicy(Policy policy) {
		if (!policies.contains(policy)) {
			policies.add(policy);
		}
	}
	
	public void uninstallPolicy(Policy policy) {
		policies.remove(policy);
	}
	
	@Override
	public void handleEvent(Event e) {
		//for each policy
			//if enabled && triggered by this event
				//if checkConditions == true
					//execute policy
	}

}
