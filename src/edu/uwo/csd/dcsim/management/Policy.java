package edu.uwo.csd.dcsim.management;

import java.lang.reflect.*;
import java.util.*;

import edu.uwo.csd.dcsim.core.*;

public abstract class Policy {

	public final String EXECUTE_METHOD_NAME = "execute";
	
	private boolean enabled = true;
	private ArrayList<Class<?>> requiredCapabilities = new ArrayList<Class<?>>();
	
	protected AutonomicManager manager;
	protected Simulation simulation;
	
	private Map<Class<?>, Method> eventMethods = new HashMap<Class<?>, Method>();
	
	public Policy(Class<?>... capabilities) {
		for (Class<?> type : capabilities) {
			requiredCapabilities.add(type);
		}
		
		//build a map of the available event methods
		for (Method method : this.getClass().getMethods()) {
			//check if the method is an "execute" method
			if (method.getName().equals(EXECUTE_METHOD_NAME)) {
				Class<?> parameterTypes[] = method.getParameterTypes();
				//check that there is only one parameter, and that parameter is an Event subclass
				if (parameterTypes.length == 1 && Event.class.isAssignableFrom(parameterTypes[0].getClass())) {
					//add to the map
					eventMethods.put(parameterTypes[0].getClass(), method);
				}
			}
		}
		
	}
	
	public boolean checkCapabilities(AutonomicManager m) {
		
		for (Class<?> type : requiredCapabilities) {
			if (m.getCapability(type) == null)
				return false;
		}
		
		return true;
	}
	
	public <T extends Event> boolean execute(T e, AutonomicManager manager) {
		
		//ensure that this policy is currently enabled
		if (!enabled) {
			return false;
		}
		
		//set up context for policy execution
		this.manager = manager;
		this.simulation = e.getSimulation(); //available through event anyways, but this is a bit more clear
		
		//look for an execute method for this event type
		Method m = null;
		try {
			m = this.getClass().getMethod(EXECUTE_METHOD_NAME, e.getClass());
		} catch (SecurityException e1) {
			throw new RuntimeException(e1);
		} catch (NoSuchMethodException e1) {
			return false; //return false if no method present
		}
		
		//lookup the method for this event
//		Method m = eventMethods.get(e.getClass());
		
		//invoke the method
		if (m != null) {
			try {
				m.invoke(this, e);
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
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
