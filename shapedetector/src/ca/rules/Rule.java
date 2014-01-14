package ca.rules;

import exceptions.CAException;

public interface Rule<V> {

	/**
	 * Does what has to be done before the rule starts.
	 */
	public void start() throws CAException;

	/**
	 * Applies the rule and updates the specified object.
	 * 
	 * @param object
	 * @throws CAException
	 */
	public void update(V object) throws CAException;

	/**
	 * Does what has to be done after the rule has finished.
	 */
	public void end() throws CAException;
}
