package edu.uwo.csd.dcsim.common;

/**
 * Defines a type that acts as an Abstract Factory for a class
 * 
 * @author Michael Tighe
 *
 * @param <T>
 */
public interface ObjectFactory<T> {
	public T newInstance();
}
