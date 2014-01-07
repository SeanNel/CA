package ca.shapedetector.shapes;

/**
 * An unrecognized shape.
 * 
 * @author Sean
 */
public class UnknownShape extends SDShape {

	public UnknownShape(SDShape shape) {
		super(shape);
	}

	protected SDShape identify(SDShape shape) {
		return null;
	}

}
