package ca.shapedetector.shapes;

public interface SDShape {

	/**
	 * Returns an instance of the shape described by this polygon.
	 * 
	 * @return An instance of the detected shape if detected or returns its
	 *         parameter otherwise.
	 */
	abstract AbstractShape identify(final AbstractShape shape);
}
