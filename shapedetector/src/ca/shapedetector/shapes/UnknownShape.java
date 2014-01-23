package ca.shapedetector.shapes;

import ca.shapedetector.path.SDPath;

/**
 * An unrecognized shape.
 * 
 * @author Sean
 */
public class UnknownShape extends AbstractShape {

	public UnknownShape(final SDPath path) {
		super(path, DEFAULT_DISTRIBUTION, DEFAULT_TOLERANCE);
	}

	public UnknownShape(final AbstractShape shape) {
		super(shape);
	}

	@Override
	public AbstractShape identify(final AbstractShape shape) {
		return null;
	}

}
