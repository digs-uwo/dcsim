package edu.uwo.csd.dcsim.management;

import java.util.ArrayList;
import java.util.List;

import edu.uwo.csd.dcsim.core.*;

public abstract class AutonomicManager implements SimulationEventListener {

	private ArrayList<Policy<? extends AutonomicManager>> policies = new ArrayList<Policy<? extends AutonomicManager>>();
	
	public List<Policy<? extends AutonomicManager>> getPolicies() {
		return policies;
	}
	
	public void installPolicy(Policy<? extends AutonomicManager> policy) {
		if (!policies.contains(policy)) {
			policies.add(policy);
		}
	}
	
	public void uninstallPolicy(Policy<? extends AutonomicManager> policy) {
		policies.remove(policy);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void handleEvent(Event e) {
		//send event to any policies that are triggered by it
		for (@SuppressWarnings("rawtypes") Policy policy : policies) {
			if (policy.isEnabled() && policy.getTriggerEvents().contains(e.getClass())) {
				if (policy.evaluateConditions(e, this, e.getSimulation())) {
					policy.execute(e, this, e.getSimulation());
				}
			}
		}
			
	}
}
