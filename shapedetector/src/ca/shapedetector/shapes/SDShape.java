package ca.shapedetector.shapes;

import exceptions.NullParameterException;

public interface SDShape {

	/**
	 * Returns an instance of the shape described by this polygon.
	 * 
	 * @return An instance of the detected shape if detected or returns its
	 *         parameter otherwise.
	 * @throws NullParameterException 
	 */
	abstract AbstractShape identify(final AbstractShape shape) throws NullParameterException;
}
