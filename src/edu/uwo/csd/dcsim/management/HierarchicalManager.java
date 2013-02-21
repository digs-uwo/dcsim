package edu.uwo.csd.dcsim.management;

import java.util.*;

public class HierarchicalManager {

	private AutonomicManager parent;
	private List<AutonomicManager> children = new ArrayList<AutonomicManager>();
	
	public HierarchicalManager() {
		//allow default constructor
	}
	
	public HierarchicalManager(AutonomicManager parent) {
		this.parent = parent;
	}
	
	public AutonomicManager getParent() {
		return parent;
	}
	
	public void setParent(AutonomicManager parent) {
		this.parent = parent;
	}
	
	public List<AutonomicManager> getChildren() {
		return children;
	}
	
}
