package edu.uwo.csd.dcsim.application;

import edu.uwo.csd.dcsim.core.Simulation;

/**
 * Constructs an Application instance
 * 
 * @author Michael Tighe
 *
 */
public interface ApplicationFactory {

	/**
	 * Create an application instance
	 * 
	 * @param simulation
	 * @return
	 */
	public Application createApplication(Simulation simulation);
	
	/**
	 * Called when an application is to start
	 * @param application
	 */
	public void startApplication(Application application);
	
	/**
	 * Called when an application stopped
	 * @param application
	 */
	public void stopApplication(Application application);
	
	/**
	 * Get the depth of Applications created by this Factory. Depth refers to the number of other Applications
	 * this Application depends on. A lower depth indicates that the Application depends on relatively fewer other
	 * Applications compared to others within the same Service. The Application with the lowest depth in a set of 
	 * dependant Applications depends on no other Application, and receives its work directly from the Workload.
	 * @return
	 */
	public int getDepth();
	
}
