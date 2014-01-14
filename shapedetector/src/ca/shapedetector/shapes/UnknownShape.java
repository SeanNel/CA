package ca.shapedetector.shapes;

import ca.shapedetector.path.SDPath;

/**
 * An unrecognized shape.
 * 
 * @author Sean
 */
public class UnknownShape extends AbstractShape {

	public UnknownShape(AbstractShape shape) {
		super(shape);
	}

	public UnknownShape(SDPath path) {
		super(path);
	}

	@Override
	public AbstractShape identify(AbstractShape shape) {
		return null;
	}

}
