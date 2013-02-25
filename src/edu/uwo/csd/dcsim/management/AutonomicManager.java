package edu.uwo.csd.dcsim.management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.management.capabilities.HostCapability;
import edu.uwo.csd.dcsim.management.events.RepeatingPolicyExecutionEvent;

public class AutonomicManager implements SimulationEventListener {

	private Simulation simulation;
	private ArrayList<Policy> policies = new ArrayList<Policy>();
	private Map<Class<? extends HostCapability>, Object> capabilities = new HashMap<Class<? extends HostCapability>, Object>();
	private Map<RepeatingPolicyExecutionEvent, Policy> policyExecutionEvents = new HashMap<RepeatingPolicyExecutionEvent, Policy>();
	
	private Host container = null; //if this AutonomicManager is running within a Host, it is stored here
	
	public AutonomicManager(Simulation simulation, HostCapability... capabilities) {
		this.simulation = simulation;
		
		for (HostCapability capability : capabilities) {
			addCapability(capability);
		}
	}
	
	public void addCapability(HostCapability capability) {
		capabilities.put(capability.getClass(), capability);
	}
	
	public void setContainer(Host container) {
		this.container = container;
	}
	
	public Host getContainer() {
		return container;
	}
	
	public void onContainerStart() {
		for (Policy policy : policies) {
			policy.onManagerStart();
		}
	}
	
	public void onContainerStop() {
		for (Policy policy : policies) {
			policy.onManagerStop();
		}
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
	
	public void installPolicy(Policy policy, long executionInterval, long startTime) {
		installPolicy(policy);
		RepeatingPolicyExecutionEvent event = new RepeatingPolicyExecutionEvent(simulation, this, executionInterval);
		policyExecutionEvents.put(event, policy);
		event.start(startTime);
	}
	
	public void uninstallPolicy(Policy policy) {
		policies.remove(policy);
		
		//remove policy from list of repeating policy events, if present
		RepeatingPolicyExecutionEvent key = null;
		for (Entry<RepeatingPolicyExecutionEvent, Policy> entry : policyExecutionEvents.entrySet()) {
			if (entry.getValue() == policy) {
				key = entry.getKey();
				break;
			}
		}
		if (key != null) {
			key.stop(); //stop the event from running
			policyExecutionEvents.remove(key); //remove it from the map
		}
	}
	
	@Override
	public void handleEvent(Event e) {
		
		//first, check if the Host container this manager is running in (if any) is ON
		if (container != null && !(container.getState() == Host.HostState.ON || container.getState() == Host.HostState.POWERING_ON)) {
			//if this manager is running in a host, and the host is OFF, then do not process the event
			return;
		}
		
		//if this is a repeating policy execution event, it is only forwarded to a specific policy
		if (e instanceof RepeatingPolicyExecutionEvent) {
			Policy policy = policyExecutionEvents.get(e);
			if (policy != null) {
				policy.execute(e, this);
			}
		} else {
			//send event to all policies
			for (Policy policy : policies) {
				policy.execute(e, this);
			}
		}
			
	}
}
