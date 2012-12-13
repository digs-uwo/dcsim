package edu.uwo.csd.dcsim.application;

/**
 * Represents a class that accepts incoming work for processing
 * 
 * @author Michael Tighe
 *
 */
public interface WorkConsumer {

	/**
	 * Add new work to be processed
	 * 
	 * @param work
	 */
	public void setWorkLevel(double work);
	
}
