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
		//send event to any policies that are triggered by it
		for (Policy policy : policies) {
			if (policy.isEnabled() && policy.getTriggerEvents().contains(e.getClass())) {
				if (policy.evaluateConditions(e, this)) {
					policy.execute(e, this);
				}
			}
		}
			
	}
}
