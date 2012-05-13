package edu.uwo.csd.dcsim2.common;

/**
 * Defines a type that constructs instances of a given type
 * 
 * @author Michael Tighe
 *
 * @param <T>
 */
public interface ObjectBuilder<T> {
	public T build();
}
