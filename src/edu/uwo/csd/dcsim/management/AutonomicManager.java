package edu.uwo.csd.dcsim.management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.management.capabilities.HostCapability;

public class AutonomicManager implements SimulationEventListener {

	private ArrayList<Policy> policies = new ArrayList<Policy>();
	private Map<Class<? extends HostCapability>, Object> capabilities = new HashMap<Class<? extends HostCapability>, Object>();
	
	public AutonomicManager(HostCapability... capabilities) {
		for (HostCapability capability : capabilities) {
			addCapability(capability);
		}
	}
	
	public void addCapability(HostCapability capability) {
		capabilities.put(capability.getClass(), capability);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends HostCapability> T getCapability(Class<T> type) {
		T r = (T)capabilities.get(type);
		return r;
	}
	
	public List<Policy> getPolicies() {
		return policies;
	}
	
	public void installPolicy(Policy policy) {
		if (!policies.contains(policy)) {
			//ensure that this AutonomicManager has the correct capabilities for this policy
			if (policy.checkCapabilities(this)) {
				policies.add(policy);
			} else {
				//capability check failed, treat as a programming error and kill the simulation
				throw new RuntimeException("Policy capability check failed for " + policy.getClass().toString());
			}
		}
	}
	
	public void uninstallPolicy(Policy policy) {
		policies.remove(policy);
	}
	
	@Override
	public void handleEvent(Event e) {
		//send event to policies
		for (Policy policy : policies) {
			policy.execute(e, this);
		}
			
	}
}
