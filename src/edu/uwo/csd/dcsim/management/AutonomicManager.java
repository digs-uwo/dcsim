package edu.uwo.csd.dcsim.management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.host.Host;
import edu.uwo.csd.dcsim.management.capabilities.ManagerCapability;
import edu.uwo.csd.dcsim.management.events.RepeatingPolicyExecutionEvent;

public class AutonomicManager implements SimulationEventListener {

	private boolean running = true;
	private Simulation simulation;
	private ArrayList<Policy> policies = new ArrayList<Policy>();
	private Map<Class<? extends ManagerCapability>, Object> capabilities = new HashMap<Class<? extends ManagerCapability>, Object>();
	private Map<RepeatingPolicyExecutionEvent, Policy> policyExecutionEvents = new HashMap<RepeatingPolicyExecutionEvent, Policy>();
	private Map<Policy, RepeatingPolicyExecutionEvent> policyToExectionEvent = new HashMap<Policy, RepeatingPolicyExecutionEvent>();
	
	private Host container = null; //if this AutonomicManager is running within a Host, it is stored here
	
	public AutonomicManager(Simulation simulation, ManagerCapability... capabilities) {
		this.simulation = simulation;
		
		for (ManagerCapability capability : capabilities) {
			addCapability(capability);
		}
	}
	
	public void addCapability(ManagerCapability capability) {
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
	public <T extends ManagerCapability> T getCapability(Class<T> type) {
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
				policy.onInstall();
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
		policyToExectionEvent.put(policy, event);
		event.start(startTime);
	}
	
	public void uninstallPolicy(Policy policy) {
		policies.remove(policy);
		
		//remove policy from list of repeating policy events, if present
		RepeatingPolicyExecutionEvent event = policyToExectionEvent.get(policy);
		if (event != null) {
			event.stop();
			policyExecutionEvents.remove(event);
			policyToExectionEvent.remove(policy);
		}

	}
	
	public RepeatingPolicyExecutionEvent getPolicyExecutionEvent(Policy policy) {
		return policyToExectionEvent.get(policy);
	}
	
	public void shutdown() {
		//uninstall all policies (stops repeating events)
		ArrayList<Policy> policiesCopy = new ArrayList<Policy>();
		policiesCopy.addAll(policies);
		for (Policy policy : policiesCopy) {
			uninstallPolicy(policy);
		}
		
		//set running flag to false to prevent event handling
		running = false;
	}
	
	@Override
	public void handleEvent(Event e) {

		if (!running) return;
		
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
