package edu.uwo.csd.dcsim.management;

import java.lang.reflect.*;
import java.util.*;

import edu.uwo.csd.dcsim.core.*;
import edu.uwo.csd.dcsim.management.capabilities.HostCapability;
import edu.uwo.csd.dcsim.management.events.RepeatingPolicyExecutionEvent;

public abstract class Policy {

	public final String EXECUTE_METHOD_NAME = "execute";
	
	private boolean enabled = true;
	private ArrayList<Class<? extends HostCapability>> requiredCapabilities = new ArrayList<Class<? extends HostCapability>>();
	
	protected AutonomicManager manager;
	protected Simulation simulation;
	
	public final void addRequiredCapability(Class<? extends HostCapability> hostCapability) {
		requiredCapabilities.add(hostCapability);
	}
	
	public final boolean checkCapabilities(AutonomicManager m) {
		
		for (Class<? extends HostCapability> type : requiredCapabilities) {
			if (m.getCapability(type) == null)
				return false;
		}
		
		return true;
	}
	
	public final <T extends Event> boolean execute(T e, AutonomicManager manager) {
		
		//ensure that this policy is currently enabled
		if (!enabled) {
			return false;
		}
		
		//set up context for policy execution
		this.manager = manager;
		this.simulation = e.getSimulation(); //available through event anyways, but this is a bit more clear
		
		/*
		 * Build the list of arguments for the execute method to search for. If the event is a RepeatingPolicyExecutionEvent,
		 * we want to run the execute() method with no arguments. Otherwise, we add a single argument: the Event.
		 */
		Class<?> argsType[] = null;
		Object args[] = null;
		if (!(e instanceof RepeatingPolicyExecutionEvent)) {
			argsType = new Class<?>[1];
			argsType[0] = e.getClass();
			
			args = new Object[1];
			args[0] = e;
		}
		
	
		Method m = null;
		try {
			m = this.getClass().getMethod(EXECUTE_METHOD_NAME, argsType);
		} catch (SecurityException e1) {
			throw new RuntimeException(e1);
		} catch (NoSuchMethodException e1) {
			return false; //return false if no method present
		}
		
		//invoke the method
		if (m != null) {
			try {
				m.invoke(this, args);
			} catch (IllegalArgumentException e1) {
				throw new RuntimeException(e1);
			} catch (IllegalAccessException e1) {
				throw new RuntimeException(e1);
			} catch (InvocationTargetException e1) {
				throw new RuntimeException(e1);
			}
		}
		
		return true;
		
	}
	
	public final boolean isEnabled() {
		return enabled;
	}
	
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public abstract void onInstall();
	
	public abstract void onManagerStart();
	
	public abstract void onManagerStop();
	
}
